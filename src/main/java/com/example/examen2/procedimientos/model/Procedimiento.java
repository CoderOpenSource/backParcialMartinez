package com.example.examen2.procedimientos.model;

import com.example.examen2.consultas.model.Consulta;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.paciente.model.Paciente;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "procedimientos")
public class Procedimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @OneToOne
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    private String descripcion; // Descripción del procedimiento
    private LocalDate fecha;    // Fecha del procedimiento
    private String observaciones; // Observaciones adicionales

    @Column(nullable = true)
    private String archivoUrl; // URL del archivo relacionado al procedimiento (opcional)

    public Procedimiento() {
    }

    public Procedimiento(Medico medico, Paciente paciente, Consulta consulta, String descripcion, LocalDate fecha, String observaciones, String archivoUrl) {
        this.medico = medico;
        this.paciente = paciente;
        this.consulta = consulta;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.observaciones = observaciones;
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

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    // Getters y Setters

    // (Incluye todos los getters y setters aquí)
}
