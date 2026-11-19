package br.com.fiap.saidaassistida.dto;

public class DestinoResponseDto {
    private int id;
    private String nome;
    private String tipo;
    private String endereco;
    private int qtVagas;
    private boolean acessivel;
    private boolean disponivel;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
