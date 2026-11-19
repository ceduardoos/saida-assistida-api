package br.com.fiap.saidaassistida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MoradorRequestDto extends MoradorEdicaoRequestDto {
    @NotBlank(message = "codigoConfirmacao e obrigatorio") @Size(min = 4, max = 12)
    private String codigoConfirmacao;

    public String getCodigoConfirmacao() { return codigoConfirmacao; }
    public void setCodigoConfirmacao(String codigoConfirmacao) { this.codigoConfirmacao = codigoConfirmacao; }
}
