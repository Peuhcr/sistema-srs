package com.example.model;

public class Organizacao {

    private long id;
    private String nome;
    private String descricao;
    private int totalIntegrantes;


    public Organizacao () {
    }


    public Organizacao (long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public Organizacao (long id, String nome, String descricao, int totalIntegrantes) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.totalIntegrantes = totalIntegrantes;
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


    public String getDescricao () {
        return descricao;
    }


    public void setDescricao (String descricao) {
        this.descricao = descricao;
    }

    public int getTotalIntegrantes () {
        return totalIntegrantes;
    }

    public void setTotalIntegrantes (int totalIntegrantes) {
        this.totalIntegrantes = totalIntegrantes;
    }

}
