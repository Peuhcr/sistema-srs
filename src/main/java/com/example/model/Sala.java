package com.example.model;

public class Sala {

    private long id;
    private String nome;
    private String bloco;
    private TipoSala tipoSala;
    private int capacidade;


    public enum TipoSala {

        LABORATORIO,
        SALA_PADRAO,
        SALA_GEMINADA,

    }

    public Sala () {

    }


    public Sala (long id, String nome, String bloco, TipoSala tipoSala, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.bloco = bloco;
        this.tipoSala = tipoSala;
        this.capacidade = capacidade;
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getNome () {
        return nome;
    }

    public void setNome (String nome) {
        this.nome = nome;
    }

    public String getBloco () {
        return bloco;
    }

    public void setBloco (String bloco) {
        this.bloco = bloco;
    }

    public TipoSala getTipoSala () {
        return tipoSala;
    }

    public void setTipoSala (TipoSala tipoSala) {
        this.tipoSala = tipoSala;
    }

    public void setCapacidade (int capacidade) {
        this.capacidade = capacidade;
    }

    public int getCapacidade() {
        return capacidade;
    }
}
