package com.example.examen2.reportes.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo; // Título del reporte, por ejemplo, "Atención por Médico"
    private String tipo; // Tipo de reporte (PDF, EXCEL)
    private LocalDate fechaGeneracion; // Fecha en la que se genera el reporte
    private String parametros; // Criterios usados para generar el reporte (como JSON o texto)
    private String archivoUrl; // URL al archivo generado

    public Reporte() {}

    public Reporte(String titulo, String tipo, LocalDate fechaGeneracion, String parametros, String archivoUrl) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
        this.parametros = parametros;
        this.archivoUrl = archivoUrl;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getParametros() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }
}
