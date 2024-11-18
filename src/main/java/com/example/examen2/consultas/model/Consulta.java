package com.example.examen2.consultas.model;

import com.example.examen2.citas.model.Cita;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.paciente.model.Paciente;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    private LocalDate fecha;
    private String diagnostico;

    @ElementCollection
    @CollectionTable(name = "consulta_sintomas", joinColumns = @JoinColumn(name = "consulta_id"))
    @Column(name = "sintoma")
    private List<String> sintomas;

    private String tratamiento;
    private String notas;

    @Column(nullable = false)
    private Boolean derivoProcedimiento = false; // Nuevo campo con valor por defecto false

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public List<String> getSintomas() {
        return sintomas;
    }

    public void setSintomas(List<String> sintomas) {
        this.sintomas = sintomas;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Boolean getDerivoProcedimiento() {
        return derivoProcedimiento;
    }

    public void setDerivoProcedimiento(Boolean derivoProcedimiento) {
        this.derivoProcedimiento = derivoProcedimiento;
    }
}
