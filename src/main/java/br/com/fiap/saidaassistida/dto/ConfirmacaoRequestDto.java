package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

// O cuidador so confirma necessidades; nao altera setor, nome nem coordenadas
public class ConfirmacaoRequestDto {
    @Size(max = 30)
    private String contato;
    private boolean precisaMaca;
    private boolean usaCadeiraRodas;
    @Min(0) @Max(5)
    private int qtAcompanhante;
    @Size(max = 200)
    private String barreiraAcesso;

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
    public boolean isPrecisaMaca() { return precisaMaca; }
    public void setPrecisaMaca(boolean precisaMaca) { this.precisaMaca = precisaMaca; }
    public boolean isUsaCadeiraRodas() { return usaCadeiraRodas; }
    public void setUsaCadeiraRodas(boolean usaCadeiraRodas) { this.usaCadeiraRodas = usaCadeiraRodas; }
    public int getQtAcompanhante() { return qtAcompanhante; }
    public void setQtAcompanhante(int qtAcompanhante) { this.qtAcompanhante = qtAcompanhante; }
    public String getBarreiraAcesso() { return barreiraAcesso; }
    public void setBarreiraAcesso(String barreiraAcesso) { this.barreiraAcesso = barreiraAcesso; }
}
