package br.com.fiap.saidaassistida.handler;

import java.time.LocalDateTime;
import java.util.List;

// Formato unico de erro da API (contrato_dados.md)
public class ErroResponseDto {
    private int status;
    private String erro;
    private String mensagem;
    private LocalDateTime instante;
    private String caminho;
    private List<String> campos;

    public ErroResponseDto(int status, String erro, String mensagem, String caminho, List<String> campos) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.instante = LocalDateTime.now();
        this.caminho = caminho;
        this.campos = campos;
    }

    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getInstante() { return instante; }
    public String getCaminho() { return caminho; }
    public List<String> getCampos() { return campos; }
}
