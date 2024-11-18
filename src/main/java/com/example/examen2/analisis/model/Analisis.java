package com.example.examen2.analisis.model;

import com.example.examen2.medicos.model.Medico;
import com.example.examen2.paciente.model.Paciente;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "analisis")
public class Analisis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Column(name = "tipo_analisis", nullable = false)
    private String tipoAnalisis;

    @Column(name = "resultado", columnDefinition = "TEXT")
    private String resultado;

    @Column(name = "fecha_realizacion", nullable = false)
    private LocalDate fechaRealizacion;

    @Column(name = "archivo_url", nullable = false)
    private String archivoUrl;

    // Constructor vacío
    public Analisis() {}

    // Constructor completo
    public Analisis(Paciente paciente, Medico medico, String tipoAnalisis, String resultado,
                    LocalDate fechaRealizacion, String archivoUrl) {
        this.paciente = paciente;
        this.medico = medico;
        this.tipoAnalisis = tipoAnalisis;
        this.resultado = resultado;
        this.fechaRealizacion = fechaRealizacion;
        this.archivoUrl = archivoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getTipoAnalisis() {
        return tipoAnalisis;
    }

    public void setTipoAnalisis(String tipoAnalisis) {
        this.tipoAnalisis = tipoAnalisis;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDate getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDate fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    // Getters y Setters
    // (omitir para brevedad)
}
