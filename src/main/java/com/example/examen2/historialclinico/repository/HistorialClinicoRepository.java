package com.example.examen2.historialclinico.repository;

import com.example.examen2.historialclinico.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {

    // Método para buscar el historial clínico de un paciente
    Optional<HistorialClinico> findByPacienteId(Long pacienteId);
}
