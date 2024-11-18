package com.example.examen2.historialclinico.model;

import com.example.examen2.paciente.model.Paciente;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "historiales_clinicos")
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente; // Cada paciente tiene un único historial clínico.

    private LocalDate fechaCreacion; // Fecha de creación automática.
    private LocalDate fechaUltimaActualizacion; // Última vez que se actualizó el historial.

    @Lob
    private String notas; // Resumen del historial clínico.

    private String archivoUrl; // URL del archivo PDF consolidado (opcional).

    public HistorialClinico() {
    }

    public HistorialClinico(Paciente paciente, LocalDate fechaCreacion, LocalDate fechaUltimaActualizacion, String notas, String archivoUrl) {
        this.paciente = paciente;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
        this.notas = notas;
        this.archivoUrl = archivoUrl;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(LocalDate fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }
}
