package com.example.model;

public class Organizacao {

    private String nome;
    private String descricao;
    private int quantidadeMembros;


    public Organizacao () {
    }


    public Organizacao (String nome, String descricao, int quantidadeMembros) {
        this.nome = nome;
        this.descricao = descricao;
        this.quantidadeMembros = quantidadeMembros;
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


    public int getQuantidadeMembros () {
        return quantidadeMembros;
    }


    public void setQuantidadeMembros (int quantidadeMembros) {
        this.quantidadeMembros = quantidadeMembros;
    }

}
