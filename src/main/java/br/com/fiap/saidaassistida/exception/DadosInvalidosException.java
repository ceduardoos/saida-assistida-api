package br.com.fiap.saidaassistida.exception;

// Dado de entrada invalido que o Bean Validation nao cobre -> 400
public class DadosInvalidosException extends Exception {
    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
