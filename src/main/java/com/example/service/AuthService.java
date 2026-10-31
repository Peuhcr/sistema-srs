package com.example.service;

import com.example.auth.CredencialUsuario;
import com.example.dao.UsuarioDao;
import com.example.exception.CredencialInvalidaException;
import com.example.model.Usuario;


public class AuthService {

    private final UsuarioDao usuarioDao;
    private final PasswordHasher hasher;

    public AuthService () {
        this(new UsuarioDao(), new PasswordHasher());
    }

    AuthService (UsuarioDao usuarioDao, PasswordHasher hasher) {
        this.usuarioDao = usuarioDao;
        this.hasher = hasher;
    }

    public Usuario login (String email, String senha) {

        CredencialUsuario credencial = usuarioDao.findByEmailForAuthentication(email)
                .orElseThrow(CredencialInvalidaException::new);

        if (!hasher.verificar(senha, credencial.senhaHash())) {
            throw new CredencialInvalidaException();
        }

        return usuarioDao.findById(credencial.id())
                .orElseThrow(CredencialInvalidaException::new);
    }
}
