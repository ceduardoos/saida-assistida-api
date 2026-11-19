package br.com.fiap.saidaassistida.dto;

import java.time.LocalDateTime;

public class AtendimentoResponseDto {
    private int id;
    private MoradorResponseDto morador;
    private AlertaResponseDto alerta;
    private VeiculoResponseDto veiculo;
    private DestinoResponseDto destino;
    private String responsavel;
    private String situacao;
    private String motivoPendencia;
    private LocalDateTime dataDesignacao;
    private LocalDateTime dataEmbarque;
    private LocalDateTime dataChegada;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public MoradorResponseDto getMorador() { return morador; }
    public void setMorador(MoradorResponseDto morador) { this.morador = morador; }
    public AlertaResponseDto getAlerta() { return alerta; }
    public void setAlerta(AlertaResponseDto alerta) { this.alerta = alerta; }
    public VeiculoResponseDto getVeiculo() { return veiculo; }
    public void setVeiculo(VeiculoResponseDto veiculo) { this.veiculo = veiculo; }
    public DestinoResponseDto getDestino() { return destino; }
    public void setDestino(DestinoResponseDto destino) { this.destino = destino; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public String getMotivoPendencia() { return motivoPendencia; }
    public void setMotivoPendencia(String motivoPendencia) { this.motivoPendencia = motivoPendencia; }
    public LocalDateTime getDataDesignacao() { return dataDesignacao; }
    public void setDataDesignacao(LocalDateTime dataDesignacao) { this.dataDesignacao = dataDesignacao; }
    public LocalDateTime getDataEmbarque() { return dataEmbarque; }
    public void setDataEmbarque(LocalDateTime dataEmbarque) { this.dataEmbarque = dataEmbarque; }
    public LocalDateTime getDataChegada() { return dataChegada; }
    public void setDataChegada(LocalDateTime dataChegada) { this.dataChegada = dataChegada; }
}
