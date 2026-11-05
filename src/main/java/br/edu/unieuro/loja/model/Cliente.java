package br.edu.unieuro.loja.model;

/** Representa um cliente no dominio comum da Loja UNIEURO.
 * O caso de uso Entrada de Estoque nao precisa consultar um cliente,
 * mas a classe fica disponivel para os demais casos de uso da aplicacao.
 */
public class Cliente {
    private final long id;
    private final String nome;
    private final String email;

    public Cliente(long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
