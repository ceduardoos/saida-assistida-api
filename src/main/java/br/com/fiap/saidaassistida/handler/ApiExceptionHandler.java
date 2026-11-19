package br.com.fiap.saidaassistida.handler;

import br.com.fiap.saidaassistida.exception.DadosInvalidosException;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.exception.RegraDeNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

// Todo erro sai como JSON padronizado, sem SQL nem stack trace (edital pp. 50 e 53)
@RestControllerAdvice
public class ApiExceptionHandler {

    // Registra o erro real no console do servidor; a resposta ao cliente continua generica
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponseDto> naoEncontrada(EntidadeNaoEncontradaException e, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, e.getMessage(), req, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponseDto> rotaInexistente(NoResourceFoundException e, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, "Rota nao encontrada", req, null);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponseDto> regraDeNegocio(RegraDeNegocioException e, HttpServletRequest req) {
        return montar(HttpStatus.CONFLICT, e.getMessage(), req, null);
    }

    // FK ou UNIQUE violada no Oracle (ex.: remover setor que tem moradores) -> 409
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErroResponseDto> integridade(SQLIntegrityConstraintViolationException e, HttpServletRequest req) {
        return montar(HttpStatus.CONFLICT,
                "Operacao viola um vinculo ou uma restricao do banco (registro duplicado ou com dependentes)", req, null);
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroResponseDto> dadosInvalidos(DadosInvalidosException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, e.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDto> validacao(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<String> campos = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(erro ->
                campos.add(erro.getField() + ": " + erro.getDefaultMessage()));
        return montar(HttpStatus.BAD_REQUEST, "Dados invalidos", req, campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDto> jsonInvalido(HttpMessageNotReadableException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Corpo da requisicao ausente ou mal formado", req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponseDto> metodoNaoSuportado(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Metodo nao suportado para esta rota", req, null);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErroResponseDto> banco(SQLException e, HttpServletRequest req) {
        log.error("Erro de banco em {}: {}", req.getRequestURI(), e.getMessage());
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Nao foi possivel realizar a operacao no banco de dados", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDto> inesperado(Exception e, HttpServletRequest req) {
        log.error("Erro inesperado em {}", req.getRequestURI(), e);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado", req, null);
    }

    private ResponseEntity<ErroResponseDto> montar(HttpStatus status, String mensagem, HttpServletRequest req, List<String> campos) {
        ErroResponseDto corpo = new ErroResponseDto(status.value(), status.getReasonPhrase(), mensagem, req.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
