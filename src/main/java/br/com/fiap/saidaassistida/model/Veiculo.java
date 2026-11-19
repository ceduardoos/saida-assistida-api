package br.com.fiap.saidaassistida.model;

public class Veiculo {
    private int id;
    private String nome;
    private String tipo;        // AMBULANCIA, VAN_ADAPTADA, CARRO
    private int qtAssentos;
    private int qtCadeiras;     // posicoes de cadeira de rodas (RN03: capacidades separadas)
    private int qtMacas;
    private boolean disponivel;

    public Veiculo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
