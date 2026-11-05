package br.edu.unieuro.loja.dao;

import br.edu.unieuro.loja.db.Database;
import br.edu.unieuro.loja.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acessa os dados de produto no banco H2 por JDBC. */
public class ProdutoDAO {
    private static final String SQL_LISTAR = """
            SELECT id, codigo, nome, categoria, quantidade_estoque, preco
            FROM produto
            ORDER BY nome
            """;

    public List<Produto> listarTodos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_LISTAR);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                produtos.add(new Produto(
                        resultSet.getLong("id"),
                        resultSet.getString("codigo"),
                        resultSet.getString("nome"),
                        resultSet.getString("categoria"),
                        resultSet.getInt("quantidade_estoque"),
                        resultSet.getBigDecimal("preco")
                ));
            }
        }
        return produtos;
    }
}
