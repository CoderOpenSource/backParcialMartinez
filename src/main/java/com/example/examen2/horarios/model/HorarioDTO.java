package com.example.examen2.horarios.model;

import java.time.LocalTime;

public class HorarioDTO {
    private Long id;
    private String dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Long medicoId;

    public HorarioDTO(Long id, String dia, LocalTime horaInicio, LocalTime horaFin, Long medicoId) {
        this.id = id;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.medicoId = medicoId;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
}
