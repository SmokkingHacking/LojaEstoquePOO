package br.edu.unieuro.loja.model;

import java.math.BigDecimal;

/** Representa um produto cadastrado na loja. */
public class Produto {
    private final long id;
    private final String codigo;
    private final String nome;
    private final String categoria;
    private final int quantidadeEstoque;
    private final BigDecimal preco;

    public Produto(long id, String codigo, String nome, String categoria,
                   int quantidadeEstoque, BigDecimal preco) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.categoria = categoria;
        this.quantidadeEstoque = quantidadeEstoque;
        this.preco = preco;
    }

    public long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    @Override
    public String toString() {
        return codigo + " - " + nome + " (estoque: " + quantidadeEstoque + ")";
    }
}
