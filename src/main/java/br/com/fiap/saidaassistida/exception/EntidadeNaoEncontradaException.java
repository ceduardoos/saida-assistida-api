package br.com.fiap.saidaassistida.exception;

// Recurso inexistente -> 404
public class EntidadeNaoEncontradaException extends Exception {
    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
