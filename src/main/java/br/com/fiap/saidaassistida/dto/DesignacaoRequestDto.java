package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// PUT /atendimentos/{id}/designacao
public class DesignacaoRequestDto {
    @Positive(message = "idVeiculo deve ser informado")
    private int idVeiculo;
    @Positive(message = "idDestino deve ser informado")
    private int idDestino;
    @NotBlank(message = "responsavel e obrigatorio") @Size(max = 80)
    private String responsavel;

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }
    public int getIdDestino() { return idDestino; }
    public void setIdDestino(int idDestino) { this.idDestino = idDestino; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
}
