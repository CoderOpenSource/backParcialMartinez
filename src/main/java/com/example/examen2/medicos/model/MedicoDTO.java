package com.example.examen2.medicos.model;

import java.util.List;

public class MedicoDTO {
    private Long id;
    private String ci;
    private String nombre;
    private String apellido;
    private String email;
    private String telefonoConsultorio;
    private String numeroLicencia;
    private Boolean disponibilidad;
    private List<Long> especialidadesIds;  // Solo los IDs de las especialidades
    private Long usuarioId;

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getTelefonoConsultorio() {
        return telefonoConsultorio;
    }

    public void setTelefonoConsultorio(String telefonoConsultorio) {
        this.telefonoConsultorio = telefonoConsultorio;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public Boolean getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public List<Long> getEspecialidadesIds() {
        return especialidadesIds;
    }

    public void setEspecialidadesIds(List<Long> especialidadesIds) {
        this.especialidadesIds = especialidadesIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    // Constructor y Getters y Setters
}
