package com.example.service;

import java.sql.SQLException;

import com.example.dao.SalaDao;
import com.example.model.Sala;
import java.util.Objects;

public class SalaService {

    private final SalaDao salaDao;
    

    public SalaService () {

        this(new SalaDao());
    }

    SalaService (SalaDao salaDao) {

        this.salaDao = salaDao;

    }

    public Sala criarSala (Sala sala) throws SQLException {
        
        Objects.requireNonNull(sala.getBloco(), "Bloco é obrigatório.");
        Objects.requireNonNull(sala.getNome(), "Nome é obrigatório.");
        Objects.requireNonNull(sala.getTipoSala(), "Tipo de sala é obrigatório.");
        Objects.requireNonNull(sala.getCapacidade(), "Capacidade é obrigatória.");

        

        return salaDao.insert(sala);

    }




}
