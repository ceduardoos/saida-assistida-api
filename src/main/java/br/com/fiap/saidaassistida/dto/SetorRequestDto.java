package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SetorRequestDto {
    @NotBlank(message = "nome e obrigatorio") @Size(max = 80)
    private String nome;
    @Size(max = 200)
    private String referencia;
    @NotBlank @Pattern(regexp = "BAIXO|MEDIO|ALTO|MUITO_ALTO", message = "nivelRisco deve ser BAIXO, MEDIO, ALTO ou MUITO_ALTO")
    private String nivelRisco;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getNivelRisco() { return nivelRisco; }
    public void setNivelRisco(String nivelRisco) { this.nivelRisco = nivelRisco; }
}
