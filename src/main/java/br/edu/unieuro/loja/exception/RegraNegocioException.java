package br.edu.unieuro.loja.exception;

/** Erro esperado quando os dados violam uma regra de negocio. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String message) {
        super(message);
    }
}
