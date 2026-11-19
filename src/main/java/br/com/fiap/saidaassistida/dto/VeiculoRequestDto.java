package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.*;

public class VeiculoRequestDto {
    @NotBlank @Size(max = 60)
    private String nome;
    @NotBlank @Pattern(regexp = "AMBULANCIA|VAN_ADAPTADA|CARRO", message = "tipo deve ser AMBULANCIA, VAN_ADAPTADA ou CARRO")
    private String tipo;
    @Min(0) @Max(60)
    private int qtAssentos;
    @Min(0) @Max(10)
    private int qtCadeiras;
    @Min(0) @Max(4)
    private int qtMacas;
    private boolean disponivel = true;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getQtAssentos() { return qtAssentos; }
    public void setQtAssentos(int qtAssentos) { this.qtAssentos = qtAssentos; }
    public int getQtCadeiras() { return qtCadeiras; }
    public void setQtCadeiras(int qtCadeiras) { this.qtCadeiras = qtCadeiras; }
    public int getQtMacas() { return qtMacas; }
    public void setQtMacas(int qtMacas) { this.qtMacas = qtMacas; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}
