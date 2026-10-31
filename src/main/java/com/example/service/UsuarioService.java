package com.example.service;

import com.example.dao.UsuarioDao;
import com.example.exception.AcessoNegadoException;
import com.example.exception.EmailJaCadastradoException;
import com.example.model.Usuario;
import com.example.model.Usuario.TipoUsuario;
import java.util.Objects;


public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 8;

    private final UsuarioDao usuarioDao;
    private final PasswordHasher hasher;

    public UsuarioService () {
        this(new UsuarioDao(), new PasswordHasher());
    }

    UsuarioService (UsuarioDao usuarioDao, PasswordHasher hasher) {
        this.usuarioDao = usuarioDao;
        this.hasher = hasher;
    }

    public Usuario cadastrar (Usuario ator, Usuario novo, String senhaPlana) {

        Objects.requireNonNull(ator, "ator é obrigatório");
        Objects.requireNonNull(novo, "usuário é obrigatório");

        if (ator.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new AcessoNegadoException("Apenas administradores cadastram usuários.");
        }

        if (senhaPlana == null || senhaPlana.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }

        if (novo.getTipoUsuario() == TipoUsuario.MEMBRO && novo.getOrganizacao() == null) {
            throw new IllegalArgumentException("Membro precisa estar vinculado a uma organização.");
        }
        if (novo.getTipoUsuario() == TipoUsuario.ADMIN && novo.getOrganizacao() != null) {
            throw new IllegalArgumentException("Administrador não pode estar vinculado a organização.");
        }

        if (usuarioDao.findByEmailForAuthentication(novo.getEmail()).isPresent()) {
            throw new EmailJaCadastradoException(novo.getEmail(), null);
        }

        novo.setSenhaHash(hasher.gerarHash(senhaPlana));

        return usuarioDao.insert(novo);
    }
}
