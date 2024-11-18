package com.example.examen2.citas.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private Long medicoId;
    private String medicoNombre;
    private String medicoApellido;
    private Long pacienteId;
    private String pacienteNombre;
    private String pacienteApellido;
    private String horarioDia;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private Long especialidadId;
    private String especialidadNombre;
    private String estado;
    private boolean consultaCreada; // Nuevo campo
    private String observaciones; // Campo para observaciones (nullable)

    public CitaDTO(Long id, LocalDate fecha, LocalTime hora, Long medicoId, String medicoNombre, String medicoApellido,
                   Long pacienteId, String pacienteNombre, String pacienteApellido,
                   String horarioDia, LocalTime horarioInicio, LocalTime horarioFin,
                   Long especialidadId, String especialidadNombre, String estado, boolean consultaCreada,
                   String observaciones) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.medicoId = medicoId;
        this.medicoNombre = medicoNombre;
        this.medicoApellido = medicoApellido;
        this.pacienteId = pacienteId;
        this.pacienteNombre = pacienteNombre;
        this.pacienteApellido = pacienteApellido;
        this.horarioDia = horarioDia;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.especialidadId = especialidadId;
        this.especialidadNombre = especialidadNombre;
        this.estado = estado;
        this.consultaCreada = consultaCreada;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public boolean isConsultaCreada() {
        return consultaCreada;
    }

    public void setConsultaCreada(boolean consultaCreada) {
        this.consultaCreada = consultaCreada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public String getMedicoNombre() {
        return medicoNombre;
    }

    public void setMedicoNombre(String medicoNombre) {
        this.medicoNombre = medicoNombre;
    }

    public String getMedicoApellido() {
        return medicoApellido;
    }

    public void setMedicoApellido(String medicoApellido) {
        this.medicoApellido = medicoApellido;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre;
    }

    public String getPacienteApellido() {
        return pacienteApellido;
    }

    public void setPacienteApellido(String pacienteApellido) {
        this.pacienteApellido = pacienteApellido;
    }

    public String getHorarioDia() {
        return horarioDia;
    }

    public void setHorarioDia(String horarioDia) {
        this.horarioDia = horarioDia;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(LocalTime horarioFin) {
        this.horarioFin = horarioFin;
    }

    public Long getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(Long especialidadId) {
        this.especialidadId = especialidadId;
    }

    public String getEspecialidadNombre() {
        return especialidadNombre;
    }

    public void setEspecialidadNombre(String especialidadNombre) {
        this.especialidadNombre = especialidadNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
