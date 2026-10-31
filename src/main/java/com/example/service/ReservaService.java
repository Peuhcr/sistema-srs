package com.example.service;

import com.example.dao.ReservaDao;
import com.example.dao.SalaDao;
import com.example.exception.AcessoNegadoException;
import com.example.exception.ReservaConflitoException;
import com.example.model.Reserva;
import com.example.model.Reserva.StatusReserva;
import com.example.model.Sala;
import com.example.model.Usuario;
import com.example.model.Usuario.TipoUsuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public class ReservaService {

    private final ReservaDao reservaDao;
    private final SalaDao salaDao;

    public ReservaService () {
        this(new ReservaDao(), new SalaDao());
    }

    ReservaService (ReservaDao reservaDao, SalaDao salaDao) {
        this.reservaDao = reservaDao;
        this.salaDao = salaDao;
    }

    public Reserva criar (Usuario ator, Reserva reserva) {

        Objects.requireNonNull(ator, "ator é obrigatório");
        Objects.requireNonNull(reserva, "reserva é obrigatória");
        Objects.requireNonNull(reserva.getSala(), "sala é obrigatória");

        if (ator.getOrganizacao() == null) {
            throw new AcessoNegadoException("Usuário sem organização não pode criar reservas.");
        }

        Sala sala = salaDao.findByID(reserva.getSala().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sala não encontrada."));

        LocalDateTime inicio = reserva.getDataInicio();
        LocalDateTime fim = reserva.getDataFim();

        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Data de início e fim são obrigatórias.");
        }
        if (!inicio.isBefore(fim)) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de fim.");
        }
        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível reservar no passado.");
        }
        if (reserva.getNumParticipantes() <= 0
                || reserva.getNumParticipantes() > sala.getCapacidade()) {
            throw new IllegalArgumentException(
                    "Número de participantes deve estar entre 1 e " + sala.getCapacidade() + ".");
        }

        if (reservaDao.existeConflito(sala.getId(), inicio, fim)) {
            throw new ReservaConflitoException(
                    "Já existe reserva ativa para a sala no período informado.", null);
        }

        reserva.setSala(sala);
        reserva.setResponsavel(ator);
        reserva.setOrganizacao(ator.getOrganizacao());
        reserva.setStatus(StatusReserva.PENDENTE);
        reserva.setAprovador(null);

        return reservaDao.insert(reserva);
    }

    
    public Reserva aprovar (Usuario ator, Reserva reserva) {

        exigirAdmin(ator);
        Objects.requireNonNull(reserva, "reserva é obrigatória");

        return reservaDao.aprovarReserva(reserva, ator);
    }

    
    public Reserva recusar (Usuario ator, Reserva reserva) {

        exigirAdmin(ator);
        Objects.requireNonNull(reserva, "reserva é obrigatória");

        return reservaDao.recusarReserva(reserva);
    }

    
    public List<Reserva> listarPendentes (Usuario ator) {

        exigirAdmin(ator);

        return reservaDao.findPendentes();
    }

    
    public List<Reserva> minhasReservas (Usuario ator) {

        Objects.requireNonNull(ator, "ator é obrigatório");

        return reservaDao.findByUser(ator);
    }

    private void exigirAdmin (Usuario ator) {

        Objects.requireNonNull(ator, "ator é obrigatório");

        if (ator.getTipoUsuario() != TipoUsuario.ADMIN) {
            throw new AcessoNegadoException("Apenas administradores podem executar esta ação.");
        }
    }
}
