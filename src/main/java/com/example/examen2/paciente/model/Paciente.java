package com.example.examen2.paciente.model;

import com.example.examen2.usuarios.model.Usuario;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carnet;
    private LocalDate fechaNacimiento;
    private String sexo;
    private int edad;
    private LocalDate fechaInicioSeguro;
    private LocalDate fechaFinSeguro;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    public Paciente() {
    }

    public Paciente(String carnet, LocalDate fechaNacimiento, String sexo, int edad, LocalDate fechaInicioSeguro, LocalDate fechaFinSeguro, Usuario usuario) {
        this.carnet = carnet;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.edad = edad;
        this.fechaInicioSeguro = fechaInicioSeguro;
        this.fechaFinSeguro = fechaFinSeguro;
        this.usuario = usuario;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public LocalDate getFechaInicioSeguro() {
        return fechaInicioSeguro;
    }

    public void setFechaInicioSeguro(LocalDate fechaInicioSeguro) {
        this.fechaInicioSeguro = fechaInicioSeguro;
    }

    public LocalDate getFechaFinSeguro() {
        return fechaFinSeguro;
    }

    public void setFechaFinSeguro(LocalDate fechaFinSeguro) {
        this.fechaFinSeguro = fechaFinSeguro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
