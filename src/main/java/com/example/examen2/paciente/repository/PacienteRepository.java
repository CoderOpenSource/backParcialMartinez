package com.example.examen2.paciente.repository;

import com.example.examen2.paciente.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCarnet(String carnet); // Método para buscar por carnet
}
