package com.example.exception;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException (String mensagem) {

        super(mensagem);
    }
}
