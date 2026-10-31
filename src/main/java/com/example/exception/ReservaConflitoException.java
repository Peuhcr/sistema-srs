package com.example.exception;


public class ReservaConflitoException extends RuntimeException {

    public ReservaConflitoException (String mensagem, Throwable causa) {

        super(mensagem, causa);
    }
}
