package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

// Inclusao de um morador em um alerta (POST /alertas/{id}/atendimentos ou POST /atendimentos)
public class AtendimentoRequestDto {
    @Positive(message = "idMorador deve ser informado")
    private int idMorador;
    private int idAlerta;   // ignorado na rota aninhada; obrigatorio em POST /atendimentos
    @Pattern(regexp = "SEM_CONTATO|ACESSO_IMPEDIDO|SEM_RECURSO|SAIDA_PROPRIA_PENDENTE",
            message = "motivoPendencia invalido")
    private String motivoPendencia;

    public int getIdMorador() { return idMorador; }
    public void setIdMorador(int idMorador) { this.idMorador = idMorador; }
    public int getIdAlerta() { return idAlerta; }
    public void setIdAlerta(int idAlerta) { this.idAlerta = idAlerta; }
    public String getMotivoPendencia() { return motivoPendencia; }
    public void setMotivoPendencia(String motivoPendencia) { this.motivoPendencia = motivoPendencia; }
}
