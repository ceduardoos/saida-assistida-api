package br.com.fiap.saidaassistida.model;

import java.time.LocalDateTime;

// Um morador em um alerta. Resolve o N:N Morador x Alerta e carrega os marcos da retirada.
public class Atendimento {
    public static final String AGUARDANDO = "AGUARDANDO";
    public static final String DESIGNADA = "DESIGNADA";
    public static final String EQUIPE_A_CAMINHO = "EQUIPE_A_CAMINHO";
    public static final String EMBARCADA = "EMBARCADA";
    public static final String CHEGADA_CONFIRMADA = "CHEGADA_CONFIRMADA";
    public static final String RECUSADO = "RECUSADO";

    private int id;
    private Morador morador;
    private Alerta alerta;
    private Veiculo veiculo;          // null enquanto AGUARDANDO
    private Destino destino;          // null enquanto AGUARDANDO
    private String responsavel;
    private String situacao;
    private String motivoPendencia;   // SEM_CONTATO, ACESSO_IMPEDIDO, SEM_RECURSO, SAIDA_PROPRIA_PENDENTE
    private LocalDateTime dataDesignacao;
    private LocalDateTime dataEmbarque;
    private LocalDateTime dataChegada;

    public Atendimento() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Morador getMorador() { return morador; }
    public void setMorador(Morador morador) { this.morador = morador; }
    public Alerta getAlerta() { return alerta; }
    public void setAlerta(Alerta alerta) { this.alerta = alerta; }
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    public Destino getDestino() { return destino; }
    public void setDestino(Destino destino) { this.destino = destino; }
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
