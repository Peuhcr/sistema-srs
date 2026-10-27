package com.example.exception;


public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException (String email, Throwable causa) {

    
        super ("E-mail já cadastrado." + email, causa);
    }
}
