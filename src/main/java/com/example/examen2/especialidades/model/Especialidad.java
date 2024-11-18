package com.example.examen2.especialidades.model;

import com.example.examen2.medicos.model.Medico;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @ManyToMany(mappedBy = "especialidades")
    @JsonIgnore  // Ignora la lista de médicos al serializar a JSON
    private Set<Medico> medicos;

    // Constructor sin parámetros
    public Especialidad() {
    }

    // Constructor con nombre y descripción
    public Especialidad(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Set<Medico> getMedicos() { return medicos; }
    public void setMedicos(Set<Medico> medicos) { this.medicos = medicos; }
}
