package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

// PUT /atendimentos/{id}/marcos : avanca o estado (equipe a caminho, embarque, chegada) ou registra recusa/pendencia
public class MarcoRequestDto {
    @NotBlank @Pattern(regexp = "EQUIPE_A_CAMINHO|EMBARCADA|CHEGADA_CONFIRMADA|RECUSADO|PENDENCIA",
            message = "marco deve ser EQUIPE_A_CAMINHO, EMBARCADA, CHEGADA_CONFIRMADA, RECUSADO ou PENDENCIA")
    private String marco;
    private LocalDateTime instante;   // opcional; registro de campo pode estar no passado (sem @Future)
    @Pattern(regexp = "SEM_CONTATO|ACESSO_IMPEDIDO|SEM_RECURSO|SAIDA_PROPRIA_PENDENTE", message = "motivoPendencia invalido")
    private String motivoPendencia;   // usado quando marco = PENDENCIA

    public String getMarco() { return marco; }
    public void setMarco(String marco) { this.marco = marco; }
    public LocalDateTime getInstante() { return instante; }
    public void setInstante(LocalDateTime instante) { this.instante = instante; }
    public String getMotivoPendencia() { return motivoPendencia; }
    public void setMotivoPendencia(String motivoPendencia) { this.motivoPendencia = motivoPendencia; }
}
