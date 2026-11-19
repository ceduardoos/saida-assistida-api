package br.com.fiap.saidaassistida.model;

import java.time.LocalDateTime;

public class Morador {
    private int id;
    private Setor setor;                 // 1:N Setor -> Morador
    private String nome;
    private String endereco;
    private String contato;
    private double latitude;
    private double longitude;
    private boolean precisaMaca;
    private boolean usaCadeiraRodas;
    private int qtAcompanhante;
    private String barreiraAcesso;
    private String codigoConfirmacao;    // usado pelo cuidador na pagina /confirmar/:codigo
    private LocalDateTime dataConfirmacao;
    private String situacaoCadastro;     // ATIVO ou SAIU_DA_LISTA (RN08)
    private LocalDateTime dataPedidoSaida;

    public Morador() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Setor getSetor() { return setor; }
    public void setSetor(Setor setor) { this.setor = setor; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isPrecisaMaca() { return precisaMaca; }
    public void setPrecisaMaca(boolean precisaMaca) { this.precisaMaca = precisaMaca; }
    public boolean isUsaCadeiraRodas() { return usaCadeiraRodas; }
    public void setUsaCadeiraRodas(boolean usaCadeiraRodas) { this.usaCadeiraRodas = usaCadeiraRodas; }
    public int getQtAcompanhante() { return qtAcompanhante; }
    public void setQtAcompanhante(int qtAcompanhante) { this.qtAcompanhante = qtAcompanhante; }
    public String getBarreiraAcesso() { return barreiraAcesso; }
    public void setBarreiraAcesso(String barreiraAcesso) { this.barreiraAcesso = barreiraAcesso; }
    public String getCodigoConfirmacao() { return codigoConfirmacao; }
    public void setCodigoConfirmacao(String codigoConfirmacao) { this.codigoConfirmacao = codigoConfirmacao; }
    public LocalDateTime getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDateTime dataConfirmacao) { this.dataConfirmacao = dataConfirmacao; }
    public String getSituacaoCadastro() { return situacaoCadastro; }
    public void setSituacaoCadastro(String situacaoCadastro) { this.situacaoCadastro = situacaoCadastro; }
    public LocalDateTime getDataPedidoSaida() { return dataPedidoSaida; }
    public void setDataPedidoSaida(LocalDateTime dataPedidoSaida) { this.dataPedidoSaida = dataPedidoSaida; }
}
