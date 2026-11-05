package br.edu.unieuro.loja.service;

import br.edu.unieuro.loja.dao.EstoqueDAO;
import br.edu.unieuro.loja.dao.ProdutoDAO;
import br.edu.unieuro.loja.exception.RegraNegocioException;
import br.edu.unieuro.loja.model.EntradaEstoque;
import br.edu.unieuro.loja.model.Produto;

import java.sql.SQLException;
import java.util.List;

/** Regras de negocio do caso de uso Entrada de Produtos no Estoque. */
public class EstoqueService {
    private final ProdutoDAO produtoDAO;
    private final EstoqueDAO estoqueDAO;

    public EstoqueService() {
        this.produtoDAO = new ProdutoDAO();
        this.estoqueDAO = new EstoqueDAO();
    }

    public List<Produto> listarProdutos() throws SQLException {
        return produtoDAO.listarTodos();
    }

    public EntradaEstoque registrarEntrada(long produtoId, int quantidade,
                                           String observacao) throws SQLException {
        if (produtoId <= 0) {
            throw new RegraNegocioException("Selecione um produto valido.");
        }
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade da entrada deve ser maior que zero.");
        }
        if (observacao != null && observacao.trim().length() > 500) {
            throw new RegraNegocioException("A observacao deve ter no maximo 500 caracteres.");
        }

        return estoqueDAO.registrarEntrada(produtoId, quantidade,
                observacao == null ? "" : observacao.trim());
    }
}
