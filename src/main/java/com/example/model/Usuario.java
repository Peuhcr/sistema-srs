package com.example.model;

public class Usuario {

    private int id;
    private String nome;
    private String matricula;
    private String email;
    private TipoUsuario tipoUsuario;


    public enum TipoUsuario {

        ADMIN,
        ORGANIZACAO,
    }

    public Usuario() {

    }


    public Usuario
            (int id, String nome, String matricula, String email, TipoUsuario tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getMatricula() {
        return matricula;
    }


    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public TipoUsuario getTipoUsuario () {
        return tipoUsuario;
    }


    public void setTipoUsuario (TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}