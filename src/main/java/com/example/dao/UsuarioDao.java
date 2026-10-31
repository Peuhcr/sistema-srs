package com.example.dao;

import com.example.auth.CredencialUsuario;
import com.example.config.ConectaDb;
import com.example.model.Organizacao;
import com.example.model.Usuario;
import com.example.model.Usuario.TipoUsuario;
import com.example.exception.*;
import java.sql.*;
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
                    rs.getString("tipo").toUpperCase(Locale.ROOT));


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

    public Usuario insert (Usuario usuario) {

        String sql = """
            INSERT INTO usuarios
                (nome, matricula, email, tipo, organizacoes_id, senha_hash)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        
        try (Connection conn = ConectaDb.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, usuario.getNome());
                stmt.setString(2, usuario.getMatricula());
                stmt.setString(3, usuario.getEmail());
                stmt.setString(4, usuario.getTipoUsuario().name().toLowerCase(Locale.ROOT));

                if (usuario.getOrganizacao() == null) {

                    stmt.setNull(5, Types.BIGINT);

                } else {

                    stmt.setLong(5, usuario.getOrganizacao().getId());
                }

                stmt.setString(6, usuario.getSenhaHash());

                try (ResultSet rs = stmt.executeQuery()) {

                     if (!rs.next()) {

                        throw new SQLException("INSERT não retornou o id do usuário");
                    }

                    usuario.setId(rs.getLong("id"));
                    return usuario;
                }
            } catch (SQLException e) {

                throw new RuntimeException ("Erro ao inserir usuário", e);
            }
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setMatricula(rs.getString("matricula"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTipoUsuario(
            TipoUsuario.valueOf(rs.getString("tipo").toUpperCase(Locale.ROOT))
        );

      
        long organizacaoId = rs.getLong("organizacao_id");
        
        if (!rs.wasNull()) {

            usuario.setOrganizacao(new Organizacao(

                organizacaoId, 
                rs.getString("organizacao_nome"),
                rs.getString("organizacao_descricao")
                ));
        }

        return usuario;
    }

    public void update (Usuario usuario) {

        String sql = "UPDATE usuarios SET nome = ?,  email = ?, organizacoes_id = ?, senha_hash = ?"
            + "atualizada_em = now() WHERE id = ?";

        
        try (Connection conn = ConectaDb.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, usuario.getNome());
                stmt.setString(2, usuario.getEmail());
                stmt.setLong(3, usuario.getOrganizacao().getId());
                stmt.setString(4, usuario.getSenhaHash());

            
        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {

                throw new EmailJaCadastradoException(usuario.getEmail(), e);
            }

            throw new RuntimeException("Erro ao atualizar usuário" +
                usuario.getId(), e
            );
        }



    }
}
