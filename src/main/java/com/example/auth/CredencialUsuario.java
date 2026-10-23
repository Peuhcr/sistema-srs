package com.example.auth;

import com.example.model.Usuario.TipoUsuario;

public record CredencialUsuario(

    long id, String email, String senhaHash, TipoUsuario tipo
    
) {}
