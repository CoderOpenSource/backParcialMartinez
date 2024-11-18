package com.example.examen2.citas.model;

import com.example.examen2.medicos.model.Medico;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.horarios.model.Horario;
import com.example.examen2.especialidades.model.Especialidad;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private LocalTime hora;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "horario_id")
    private Horario horario;

    @ManyToOne
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private boolean consultaCreada = false; // Indica si la consulta ya ha sido creada para esta cita

    @Column(nullable = true, length = 500) // Campo opcional para observaciones
    private String observaciones;

    // Constructor sin argumentos
    public Cita() {
    }

    public Cita(LocalDate fecha, LocalTime hora, Medico medico, Paciente paciente, Horario horario, Especialidad especialidad, String estado) {
        this.fecha = fecha;
        this.hora = hora;
        this.medico = medico;
        this.paciente = paciente;
        this.horario = horario;
        this.especialidad = especialidad;
        this.estado = estado;
        this.consultaCreada = false; // Inicialmente en false
    }

    // Getters y Setters

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

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isConsultaCreada() {
        return consultaCreada;
    }

    public void setConsultaCreada(boolean consultaCreada) {
        this.consultaCreada = consultaCreada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
