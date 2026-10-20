package com.example.dao;

import com.example.config.ConectaDb;
import com.example.model.Sala;
import com.example.model.Sala.TipoSala;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.RuntimeCryptoException;

public class SalaDao {

    public List<Sala> findAll () {

        String sql = "SELECT id, nome, bloco, tipo, capacidade FROM salas ORDER BY id";
        List<Sala> salas = new ArrayList<>();

        try (Connection conn = ConectaDb.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                salas.add(mapRow(rs));

            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao listar salas", e);

        }

        return salas;

    }


    public Optional<Sala> findByID (long id) {

        String sql = "SELECT id, nome, bloco, tipo, capacidade FROM salas WHERE id = ?";

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao buscar sala " + id, e);
        }
    }


    public Sala insert (Sala sala) {

        String sql = " INSERT INTO salas (nome, bloco, tipo, capacidade) VALUES (?, ?, ?, ?) RETURNING ID";

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sala.getNome());
            stmt.setString(2, sala.getBloco());
            stmt.setString(3, sala.getTipoSala().name().toLowerCase());
            stmt.setInt(4, sala.getCapacidade());

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    throw new RuntimeException("INSERT não retornou um id (inesperado)");
                }

                sala.setId(rs.getLong("id"));
                return sala;
            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao inserir sala", e);
        }
    }


    public void update (Sala sala) {

        String sql = "UPDATE salas SET nome = ?, bloco = ?, tipo = ?, capacidade = ?, " 
                    + "atualizada_em = now() WHERE id = ? ";

        try (Connection conn = ConectaDb.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sala.getNome());
            stmt.setString(2, sala.getBloco());
            stmt.setString(3, sala.getTipoSala().name().toLowerCase());
            stmt.setInt(4, sala.getCapacidade());
            stmt.setLong(5, sala.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao atualizar sala", e);
        }
    }

    public void delete (long id) {

        String sql = "DELETE FROM salas WHERE id =  ?";

        try (Connection conn = ConectaDb.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, id);
                stmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao deletar sala", e);
        }
    }

    public List <Sala> findByTipo (TipoSala tipo) {

        List <Sala> salas = new ArrayList<>();
        String sql = "SELECT id, nome, bloco, tipo, capacidade, FROM salas WHERE tipo = ?";
                   

        try (Connection conn = ConectaDb.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, tipo.name());

                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {

                        salas.add(mapRow(rs));
                    }
                }
            } catch (SQLException e) {

                throw new RuntimeException("Erro ao buscar salas por tipo", e);
            }
            return salas;
    }


    private Sala mapRow (ResultSet rs) throws SQLException {

        Sala sala = new Sala();
        
        sala.setId(rs.getLong("id"));
        sala.setNome(rs.getString("nome"));
        sala.setBloco(rs.getString("bloco"));
        sala.setTipoSala(TipoSala.valueOf(rs.getString("tipo").toUpperCase()));
        sala.setCapacidade(rs.getInt("capacidade"));
        return sala;
    }

}
