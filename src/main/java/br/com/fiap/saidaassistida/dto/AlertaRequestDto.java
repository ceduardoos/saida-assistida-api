package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class AlertaRequestDto {
    @Positive(message = "idSetor deve ser informado")
    private int idSetor;
    @NotBlank @Pattern(regexp = "AMARELO|LARANJA|VERMELHO", message = "nivel deve ser AMARELO, LARANJA ou VERMELHO")
    private String nivel;
    private LocalDateTime dataAbertura;      // opcional: se nulo, usa agora
    private LocalDateTime dataEncerramento;  // opcional
    @Pattern(regexp = "ABERTO|ENCERRADO", message = "situacao deve ser ABERTO ou ENCERRADO")
    private String situacao;                 // opcional: se nulo, ABERTO

    public int getIdSetor() { return idSetor; }
    public void setIdSetor(int idSetor) { this.idSetor = idSetor; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDateTime dataEncerramento) { this.dataEncerramento = dataEncerramento; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}
