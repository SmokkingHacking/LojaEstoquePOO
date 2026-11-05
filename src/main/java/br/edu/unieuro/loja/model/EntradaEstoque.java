package br.edu.unieuro.loja.model;

import java.time.LocalDateTime;

/** Representa uma movimentacao de entrada de unidades no estoque. */
public class EntradaEstoque {
    private final long id;
    private final long produtoId;
    private final int quantidade;
    private final LocalDateTime dataEntrada;
    private final String observacao;

    public EntradaEstoque(long id, long produtoId, int quantidade,
                          LocalDateTime dataEntrada, String observacao) {
        this.id = id;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.dataEntrada = dataEntrada;
        this.observacao = observacao;
    }

    public long getId() {
        return id;
    }

    public long getProdutoId() {
        return produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public String getObservacao() {
        return observacao;
    }
}
