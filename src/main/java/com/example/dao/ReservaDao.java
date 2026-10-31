package com.example.dao;

import com.example.config.ConectaDb;
import com.example.exception.ReservaConflitoException;
import com.example.model.Organizacao;
import com.example.model.Reserva;
import com.example.model.Reserva.StatusReserva;
import com.example.model.Sala;
import com.example.model.Sala.TipoSala;
import com.example.model.Usuario;
import com.example.model.Usuario.TipoUsuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class ReservaDao {

    private static final String SELECT_RESERVA = """
            SELECT r.id,
                   r.data_inicio, r.data_fim, r.status, r.titulo, r.descricao,
                   r.num_participantes,
                   s.id AS sala_id, s.nome AS sala_nome, s.bloco AS sala_bloco,
                   s.tipo AS sala_tipo, s.capacidade AS sala_capacidade,
                   resp.id AS responsavel_id, resp.nome AS responsavel_nome,
                   resp.matricula AS responsavel_matricula,
                   resp.email AS responsavel_email, resp.tipo AS responsavel_tipo,
                   apr.id AS aprovador_id, apr.nome AS aprovador_nome,
                   apr.matricula AS aprovador_matricula,
                   apr.email AS aprovador_email, apr.tipo AS aprovador_tipo,
                   o.id AS organizacao_id, o.nome AS organizacao_nome,
                   o.descricao AS organizacao_descricao
            FROM reservas r
            JOIN salas s ON s.id = r.salas_id
            JOIN usuarios resp ON resp.id = r.responsavel_id
            LEFT JOIN usuarios apr ON apr.id = r.aprovador_id
            JOIN organizacoes o ON o.id = r.organizacoes_id
            """;


    public Reserva aprovarReserva (Reserva reserva, Usuario aprovador) {

        Objects.requireNonNull(aprovador, "aprovador é obrigatório para aprovar");

        return atualizarStatus(reserva, StatusReserva.APROVADA, aprovador);
    }


    public Reserva recusarReserva (Reserva reserva) {

        return atualizarStatus(reserva, StatusReserva.RECUSADA, null);
    }


    private Reserva atualizarStatus (Reserva reserva, StatusReserva novoStatus, Usuario aprovador) {

        Objects.requireNonNull(reserva, "reserva é obrigatória");

        String sql = """
                UPDATE reservas
                   SET status = ?, aprovador_id = ?, atualizada_em = now()
                 WHERE id = ? AND status = 'pendente'
                """;

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus.name().toLowerCase(Locale.ROOT));

            if (aprovador == null) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, aprovador.getId());
            }

            stmt.setLong(3, reserva.getId());

            if (stmt.executeUpdate() == 0) {
                throw new IllegalStateException(
                        "A reserva " + reserva.getId() + " não está pendente ou não existe");
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao atualizar status da reserva " + reserva.getId(), e);
        }

        return findById(reserva.getId()).orElseThrow(() -> new IllegalStateException(
                "Reserva " + reserva.getId() + " não encontrada após atualização"));
    }


    public List<Reserva> findPendentes () {

        String sql = SELECT_RESERVA + "WHERE r.status = 'pendente' ORDER BY r.data_inicio";

        return buscarLista(sql, null);
    }


    public List<Reserva> findByUser (Usuario usuario) {

        Objects.requireNonNull(usuario, "usuário é obrigatório");

        String sql = SELECT_RESERVA + "WHERE r.responsavel_id = ? ORDER BY r.data_inicio DESC";

        return buscarLista(sql, usuario.getId());
    }


    public List<Reserva> findAll () {

        String sql = SELECT_RESERVA + "ORDER BY r.data_inicio DESC";

        return buscarLista(sql, null);
    }


    public boolean existeConflito (long salaId, LocalDateTime inicio, LocalDateTime fim) {

        Objects.requireNonNull(inicio, "início é obrigatório");
        Objects.requireNonNull(fim, "fim é obrigatório");

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                      FROM reservas
                     WHERE salas_id = ?
                       AND status IN ('pendente', 'aprovada')
                       AND data_inicio < ?
                       AND data_fim > ?
                )
                """;

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, salaId);
            stmt.setObject(2, fim);
            stmt.setObject(3, inicio);

            try (ResultSet rs = stmt.executeQuery()) {

                return rs.next() && rs.getBoolean(1);
            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao verificar conflito de reserva", e);
        }
    }


    public Optional<Reserva> findById (Reserva reserva) {

        Objects.requireNonNull(reserva, "reserva é obrigatória");

        return findById(reserva.getId());
    }


    public Optional<Reserva> findById (long id) {

        String sql = SELECT_RESERVA + "WHERE r.id = ?";

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao buscar reserva " + id, e);
        }
    }


    public Reserva insert (Reserva reserva) {

        Objects.requireNonNull(reserva, "reserva é obrigatória");
        Objects.requireNonNull(reserva.getSala(), "sala é obrigatória");
        Objects.requireNonNull(reserva.getResponsavel(), "responsável é obrigatório");
        Objects.requireNonNull(reserva.getOrganizacao(), "organização é obrigatória");

        String sql = """
                INSERT INTO reservas
                    (salas_id, responsavel_id, organizacoes_id, data_inicio, data_fim,
                     status, titulo, descricao, aprovador_id, num_participantes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        StatusReserva status = reserva.getStatus() == null
                ? StatusReserva.PENDENTE
                : reserva.getStatus();

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reserva.getSala().getId());
            stmt.setLong(2, reserva.getResponsavel().getId());
            stmt.setLong(3, reserva.getOrganizacao().getId());
            stmt.setObject(4, reserva.getDataInicio());
            stmt.setObject(5, reserva.getDataFim());
            stmt.setString(6, status.name().toLowerCase(Locale.ROOT));
            stmt.setString(7, reserva.getTitulo());
            stmt.setString(8, reserva.getDescricao());

            if (reserva.getAprovador() == null) {
                stmt.setNull(9, Types.BIGINT);
            } else {
                stmt.setLong(9, reserva.getAprovador().getId());
            }

            stmt.setInt(10, reserva.getNumParticipantes());

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException("INSERT não retornou o id da reserva.");
                }

                reserva.setId(rs.getLong("id"));
            }

        } catch (SQLException e) {

            if ("23P01".equals(e.getSQLState())) {
                throw new ReservaConflitoException(
                        "Já existe reserva ativa para a sala no período informado.", e);
            }

            throw new RuntimeException("Erro ao inserir reserva.", e);
        }

        reserva.setStatus(status);

        return reserva;
    }


    public void update (Reserva reserva) {

        Objects.requireNonNull(reserva, "reserva é obrigatória");
        Objects.requireNonNull(reserva.getSala(), "sala é obrigatória");

        String sql = """
                UPDATE reservas
                   SET salas_id = ?, data_inicio = ?, data_fim = ?,
                       titulo = ?, descricao = ?, num_participantes = ?,
                       atualizada_em = now()
                 WHERE id = ?
                """;

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, reserva.getSala().getId());
            stmt.setObject(2, reserva.getDataInicio());
            stmt.setObject(3, reserva.getDataFim());
            stmt.setString(4, reserva.getTitulo());
            stmt.setString(5, reserva.getDescricao());
            stmt.setInt(6, reserva.getNumParticipantes());
            stmt.setLong(7, reserva.getId());

            if (stmt.executeUpdate() == 0) {
                throw new SQLDataException("Reserva não encontrada: " + reserva.getId());
            }

        } catch (SQLException e) {

            if ("23P01".equals(e.getSQLState())) {
                throw new ReservaConflitoException(
                        "Já existe reserva ativa para a sala no período informado.", e);
            }

            throw new RuntimeException("Erro ao atualizar reserva " + reserva.getId(), e);
        }
    }


    public void delete (long id) {

        String sql = "DELETE FROM reservas WHERE id = ?";

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao deletar reserva " + id, e);
        }
    }


    private List<Reserva> buscarLista (String sql, Long responsavelId) {

        List<Reserva> reservas = new ArrayList<>();

        try (Connection conn = ConectaDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (responsavelId != null) {
                stmt.setLong(1, responsavelId);
            }

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    reservas.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao listar reservas", e);
        }

        return reservas;
    }


    private Reserva mapRow (ResultSet rs) throws SQLException {

        Reserva reserva = new Reserva();

        reserva.setId(rs.getLong("id"));
        reserva.setDataInicio(rs.getTimestamp("data_inicio").toLocalDateTime());
        reserva.setDataFim(rs.getTimestamp("data_fim").toLocalDateTime());
        reserva.setStatus(StatusReserva.valueOf(
                rs.getString("status").toUpperCase(Locale.ROOT)));
        reserva.setTitulo(rs.getString("titulo"));
        reserva.setDescricao(rs.getString("descricao"));
        reserva.setNumParticipantes(rs.getInt("num_participantes"));

        reserva.setSala(new Sala(
                rs.getLong("sala_id"),
                rs.getString("sala_nome"),
                rs.getString("sala_bloco"),
                TipoSala.valueOf(rs.getString("sala_tipo").toUpperCase(Locale.ROOT)),
                rs.getInt("sala_capacidade")));

        reserva.setResponsavel(mapUsuario(rs, "responsavel", false));
        reserva.setAprovador(mapUsuario(rs, "aprovador", true));

        reserva.setOrganizacao(new Organizacao(
                rs.getLong("organizacao_id"),
                rs.getString("organizacao_nome"),
                rs.getString("organizacao_descricao")));

        return reserva;
    }


    private Usuario mapUsuario (ResultSet rs, String prefixo, boolean podeSerNulo)
            throws SQLException {

        long id = rs.getLong(prefixo + "_id");

        if (podeSerNulo && rs.wasNull()) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setId(id);
        usuario.setNome(rs.getString(prefixo + "_nome"));
        usuario.setMatricula(rs.getString(prefixo + "_matricula"));
        usuario.setEmail(rs.getString(prefixo + "_email"));
        usuario.setTipoUsuario(TipoUsuario.valueOf(
                rs.getString(prefixo + "_tipo").toUpperCase(Locale.ROOT)));

        return usuario;
    }
}
