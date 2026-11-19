package br.com.fiap.saidaassistida.model;

public class Setor {
    private int id;
    private String nome;
    private String referencia;
    private String nivelRisco; // BAIXO, MEDIO, ALTO, MUITO_ALTO

    public Setor() {}
    public Setor(int id, String nome, String referencia, String nivelRisco) {
        this.id = id; this.nome = nome; this.referencia = referencia; this.nivelRisco = nivelRisco;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getNivelRisco() { return nivelRisco; }
    public void setNivelRisco(String nivelRisco) { this.nivelRisco = nivelRisco; }
}
