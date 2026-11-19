package br.com.fiap.saidaassistida.dto;

import java.time.LocalDateTime;

public class AlertaResponseDto {
    private int id;
    private SetorResponseDto setor;
    private String nivel;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataEncerramento;
    private String situacao;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public SetorResponseDto getSetor() { return setor; }
    public void setSetor(SetorResponseDto setor) { this.setor = setor; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDateTime dataEncerramento) { this.dataEncerramento = dataEncerramento; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}
