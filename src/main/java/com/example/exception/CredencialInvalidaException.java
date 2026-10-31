package com.example.exception;

public class CredencialInvalidaException extends RuntimeException {

    public CredencialInvalidaException () {

        // Mensagem genérica: não revela se o e-mail existe ou se a senha está errada.
        super("E-mail ou senha inválidos.");
    }
}
