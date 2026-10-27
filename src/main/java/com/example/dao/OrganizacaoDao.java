package com.example.dao;

import com.example.auth.CredencialUsuario;
import com.example.config.ConectaDb;
import com.example.model.Organizacao;
import com.example.model.Usuario;
import java.sql.*;
import java.util.Locale;
import java.util.Optional;

public class OrganizacaoDao {

    /* CRUD 
    
    
    */


    public Organizacao insert (Organizacao org) {

        String sql = """        
            INSERT INTO organizacoes (nome, descricao) 
            VALUE (?, ?)
            RETURNING id    
        """;

        try (Connection conn = ConectaDb.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString (1, org.getNome());
                stmt.setString (2, org.getDescricao());
                

                try (ResultSet rs = stmt.executeQuery()) {

                    rs.next();
                    org.setId(rs.getLong("id"));             
               }

            } catch (SQLException e) {

                throw new RuntimeException("Erro ao inserir organização.", e);
            }
        
        return org;
    }

    public void update (Organizacao org) {

        String sql = "UPDATE organizacoes SET nome = ?, descricao = ?, atualizada_em = now() WHERE id = ?";

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, org.getNome());
            stmt.setString(2, org.getDescricao());
            stmt.setLong(3, org.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao atualizar organização", e);
        }
    }


    
    

        



}



