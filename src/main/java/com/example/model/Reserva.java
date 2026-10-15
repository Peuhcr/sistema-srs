package com.example.model;

import java.time.LocalDateTime;


public class Reserva {

    private int id;
    private Sala  sala;
    private Usuario responsavel;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusReserva status;


    public enum StatusReserva {

        PENDENTE,
        APROVADA,
        RECUSADA,
    }


    public Reserva () {
    }


    public Reserva (int id, Sala sala, Usuario responsavel, LocalDateTime dataInicio,
                   LocalDateTime dataFim, StatusReserva status) {
        this.id = id;
        this.sala = sala;
        this.responsavel = responsavel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
    }


    public int getId () {
        return id;
    }


    public void setId (int id) {
        this.id = id;
    }


    public Sala getSala () {
        return sala;
    }


    public void setSala (Sala sala) {
        this.sala = sala;
    }


    public Usuario getResponsavel () {
        return responsavel;
    }


    public void setResponsavel (Usuario responsavel) {
        this.responsavel = responsavel;
    }


    public LocalDateTime getDataInicio () {
        return dataInicio;
    }


    public void setDataInicio (LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }


    public LocalDateTime getDataFim () {
        return dataFim;
    }


    public void setDataFim (LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }


    public StatusReserva getStatus () {
        return status;
    }


    public void setStatus (StatusReserva status) {
        this.status = status;
    }

}
