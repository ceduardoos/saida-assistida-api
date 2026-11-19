package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.*;

public class DestinoRequestDto {
    @NotBlank @Size(max = 100)
    private String nome;
    @NotBlank @Pattern(regexp = "CASA_FAMILIAR|ESCOLA_ABRIGO|ABRIGO_ACESSIVEL|UNIDADE_SAUDE",
            message = "tipo deve ser CASA_FAMILIAR, ESCOLA_ABRIGO, ABRIGO_ACESSIVEL ou UNIDADE_SAUDE")
    private String tipo;
    @NotBlank @Size(max = 200)
    private String endereco;
    @Min(0) @Max(5000)
    private int qtVagas;
    private boolean acessivel;
    private boolean disponivel = true;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public int getQtVagas() { return qtVagas; }
    public void setQtVagas(int qtVagas) { this.qtVagas = qtVagas; }
    public boolean isAcessivel() { return acessivel; }
    public void setAcessivel(boolean acessivel) { this.acessivel = acessivel; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}
