package com.example.dao;

import com.example.auth.CredencialUsuario;
import com.example.config.ConectaDb;
import com.example.model.Organizacao;
import com.example.model.Usuario;
import com.example.model.Sala.TipoSala;
import com.example.model.Usuario.TipoUsuario;
import com.example.auth.CredencialUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class UsuarioDao {

    private static final String SELECT_USER = """
            SELECT u.id, u.nome, u.matricula, u.email, u.tipo,
           u.organizacoes_id AS organizacao_id,
           o.nome AS organizacao_nome,
           o.descricao AS organizacao_descricao
            FROM usuarios u
            LEFT JOIN organizacoes o ON o.id = u.organizacoes_id
            """;


    public Optional <Usuario> findById (long id) {

        String sql = SELECT_USER + "WHERE u.id = ?";
         
        try (Connection conn = ConectaDb.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, id);

                try (ResultSet rs = stmt.executeQuery()) {

                    return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
                } 
        } catch (SQLException e) {

            throw new RuntimeException("Erro ao buscar usuário." + id, e);
        }
    } 

    public Optional <CredencialUsuario> findByEmailForAuthentication (String email) {

        String sql = """
            SELECT u.id, u.email, u.senha_hash, u.tipo
            FROM usuarios u
            WHERE u.email = ?
            """;

        try (Connection conn = ConectaDb.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, email);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (!rs.next()) {

                        return Optional.empty();
                    }

                TipoUsuario tipo = TipoUsuario.valueOf(
                    rs.getString("tipo").toUpperCase());


                return Optional.of(new CredencialUsuario (
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("senha_hash"),
                        tipo));
                }
        } catch (SQLException e) {

            throw new RuntimeException("Erro de autenticação com as credenciais do usuário", e);
        }
     }


  
            

         









}
