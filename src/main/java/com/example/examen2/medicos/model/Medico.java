package com.example.examen2.medicos.model;
import com.example.examen2.especialidades.model.Especialidad;
import com.example.examen2.horarios.model.Horario;
import com.example.examen2.usuarios.model.Usuario;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@DiscriminatorValue("MEDICO")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ci; // Campo para el carnet de identidad

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Referencia al usuario con rol médico

    @ManyToMany
    @JoinTable(
            name = "medico_especialidad",
            joinColumns = @JoinColumn(name = "medico_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    private Set<Especialidad> especialidades;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Horario> horarios;

    // Atributos adicionales específicos del médico
    private String telefonoConsultorio;
    private String numeroLicencia;
    private Boolean disponibilidad; // Indica si está activo o inactivo

    // Constructor vacío
    public Medico() {
    }

    // Constructor con parámetros
    public Medico(String ci, Usuario usuario, Set<Especialidad> especialidades, String telefonoConsultorio, String numeroLicencia, Boolean disponibilidad) {
        this.ci = ci;
        this.usuario = usuario;
        this.especialidades = especialidades;
        this.telefonoConsultorio = telefonoConsultorio;
        this.numeroLicencia = numeroLicencia;
        this.disponibilidad = disponibilidad;
        this.usuario.setContrasena(ci); // Establecer ci como contraseña
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCi() { return ci; }
    public void setCi(String ci) {
        this.ci = ci;
        if (this.usuario != null) {
            this.usuario.setContrasena(ci); // Actualizar la contraseña del usuario con el CI
        }
    }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (this.ci != null) {
            this.usuario.setContrasena(this.ci); // Establecer la contraseña del usuario como el CI
        }
    }

    public Set<Especialidad> getEspecialidades() { return especialidades; }
    public void setEspecialidades(Set<Especialidad> especialidades) { this.especialidades = especialidades; }

    public Set<Horario> getHorarios() { return horarios; }
    public void setHorarios(Set<Horario> horarios) { this.horarios = horarios; }

    public String getTelefonoConsultorio() { return telefonoConsultorio; }
    public void setTelefonoConsultorio(String telefonoConsultorio) { this.telefonoConsultorio = telefonoConsultorio; }

    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }

    public Boolean getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(Boolean disponibilidad) { this.disponibilidad = disponibilidad; }
}
