package br.com.fiap.saidaassistida.exception;

// Regra do dominio violada (compatibilidade, duplicidade, ocupacao, vinculo) -> 409
public class RegraDeNegocioException extends Exception {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
