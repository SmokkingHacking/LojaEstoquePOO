package br.edu.unieuro.loja.dao;

import br.edu.unieuro.loja.db.Database;
import br.edu.unieuro.loja.model.EntradaEstoque;
import br.edu.unieuro.loja.exception.RegraNegocioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/** Grava a entrada e atualiza o saldo na mesma transacao. */
public class EstoqueDAO {

    /**
     * A transacao garante que o saldo e o historico sejam gravados juntos.
     * Se uma das etapas falhar, a operacao inteira e desfeita.
     */
    public EntradaEstoque registrarEntrada(long produtoId, int quantidade,
                                           String observacao) throws SQLException {
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int estoqueAtual;
                String sqlBloquearProduto =
                        "SELECT quantidade_estoque FROM produto WHERE id = ? FOR UPDATE";

                try (PreparedStatement statement = connection.prepareStatement(sqlBloquearProduto)) {
                    statement.setLong(1, produtoId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new RegraNegocioException("O produto selecionado nao foi encontrado.");
                        }
                        estoqueAtual = resultSet.getInt("quantidade_estoque");
                    }
                }

                if (quantidade > Integer.MAX_VALUE - estoqueAtual) {
                    throw new RegraNegocioException("A quantidade resultante excede o limite permitido.");
                }

                String sqlAtualizar = """
                        UPDATE produto
                        SET quantidade_estoque = quantidade_estoque + ?
                        WHERE id = ?
                        """;
                try (PreparedStatement statement = connection.prepareStatement(sqlAtualizar)) {
                    statement.setInt(1, quantidade);
                    statement.setLong(2, produtoId);
                    int linhasAlteradas = statement.executeUpdate();
                    if (linhasAlteradas != 1) {
                        throw new SQLException("Nao foi possivel atualizar o saldo do produto.");
                    }
                }

                String sqlInserir = """
                        INSERT INTO entrada_estoque (produto_id, quantidade, observacao)
                        VALUES (?, ?, ?)
                        """;
                long entradaId;
                LocalDateTime dataEntrada;
                try (PreparedStatement statement = connection.prepareStatement(
                        sqlInserir, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setLong(1, produtoId);
                    statement.setInt(2, quantidade);
                    statement.setString(3, observacao == null || observacao.isBlank()
                            ? null : observacao.trim());

                    int linhasInseridas = statement.executeUpdate();
                    if (linhasInseridas != 1) {
                        throw new SQLException("Nao foi possivel gravar o historico da entrada.");
                    }
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            throw new SQLException("O banco nao retornou o ID da entrada.");
                        }
                        entradaId = generatedKeys.getLong(1);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT data_entrada FROM entrada_estoque WHERE id = ?")) {
                    statement.setLong(1, entradaId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException("Nao foi possivel consultar a data da entrada gravada.");
                        }
                        dataEntrada = resultSet.getTimestamp("data_entrada").toLocalDateTime();
                    }
                }

                connection.commit();
                return new EntradaEstoque(entradaId, produtoId, quantidade, dataEntrada,
                        observacao == null ? "" : observacao.trim());
            } catch (SQLException | RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }
}
