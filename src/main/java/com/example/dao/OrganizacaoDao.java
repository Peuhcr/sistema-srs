package com.example.dao;

import com.example.config.ConectaDb;
import com.example.model.Organizacao;
import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class OrganizacaoDao {

    public Organizacao insert (Organizacao org) {

        String sql = """        
            INSERT INTO organizacoes (nome, descricao) 
            VALUES (?, ?)
            RETURNING id    
        """;

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, org.getNome());
            stmt.setString(2, org.getDescricao());

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException("INSERT não retornou o id da organização.");
                }

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

            if (stmt.executeUpdate() == 0) {
                throw new SQLDataException("ID não encontrado: " + org.getId());
            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao atualizar organização", e);
        }
    }

    public List<Organizacao> findAll () {

        List<Organizacao> orgs = new ArrayList<>();

        String sql = """
            SELECT o.id, o.nome, o.descricao,
                   COUNT(u.id) AS total_integrantes
            FROM organizacoes o
            LEFT JOIN usuarios u ON u.organizacoes_id = o.id
            GROUP BY o.id, o.nome, o.descricao
            ORDER BY o.nome
        """;

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orgs.add(mapRow(rs));
            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao listar organizações.", e);
        }

        return orgs;
    }

    public Optional<Organizacao> findById (long id) {

        String sql = """
            SELECT o.id, o.nome, o.descricao,
                   COUNT(u.id) AS total_integrantes
            FROM organizacoes o
            LEFT JOIN usuarios u ON u.organizacoes_id = o.id
            WHERE o.id = ?
            GROUP BY o.id, o.nome, o.descricao
        """;

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao buscar organização " + id, e);
        }
    }

    private Organizacao mapRow (ResultSet rs) throws SQLException {

        Organizacao org = new Organizacao();

        org.setId(rs.getLong("id"));
        org.setNome(rs.getString("nome"));
        org.setDescricao(rs.getString("descricao"));
        org.setTotalIntegrantes(rs.getInt("total_integrantes"));

        return org;
    }

}



