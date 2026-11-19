package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.*;

public class MoradorRequestDto {
    private int idSetor;   // obrigatorio em /moradores; ignorado em /setores/{id}/moradores
    @NotBlank(message = "nome e obrigatorio") @Size(max = 100)
    private String nome;
    @NotBlank(message = "endereco e obrigatorio") @Size(max = 200)
    private String endereco;
    @Size(max = 30)
    private String contato;
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private double latitude;
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private double longitude;
    private boolean precisaMaca;
    private boolean usaCadeiraRodas;
    @Min(0) @Max(5)
    private int qtAcompanhante;
    @Size(max = 200)
    private String barreiraAcesso;
    @NotBlank(message = "codigoConfirmacao e obrigatorio") @Size(min = 4, max = 12)
    private String codigoConfirmacao;

    public int getIdSetor() { return idSetor; }
    public void setIdSetor(int idSetor) { this.idSetor = idSetor; }
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
}
