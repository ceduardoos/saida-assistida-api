package br.com.fiap.saidaassistida.model;

import java.time.LocalDateTime;

public class Alerta {
    private int id;
    private Setor setor;
    private String nivel;      // AMARELO, LARANJA, VERMELHO
    private LocalDateTime dataAbertura;
    private LocalDateTime dataEncerramento;
    private String situacao;   // ABERTO, ENCERRADO

    public Alerta() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Setor getSetor() { return setor; }
    public void setSetor(Setor setor) { this.setor = setor; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDateTime dataEncerramento) { this.dataEncerramento = dataEncerramento; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}
