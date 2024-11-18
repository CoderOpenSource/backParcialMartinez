package com.example.examen2.analisis.repository;

import com.example.examen2.analisis.model.Analisis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AnalisisRepository extends JpaRepository<Analisis, Long> {
    List<Analisis> findByPacienteId(Long pacienteId);
    List<Analisis> findByMedicoId(Long medicoId);
    @Query("SELECT a FROM Analisis a " +
            "WHERE (:fechaInicio IS NULL OR a.fechaRealizacion >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR a.fechaRealizacion <= :fechaFin) " +
            "AND (:pacienteId IS NULL OR a.paciente.id = :pacienteId) " +
            "AND (:medicoId IS NULL OR a.medico.id = :medicoId)")
    List<Analisis> filtrarAnalisis(@Param("fechaInicio") LocalDate fechaInicio,
                                   @Param("fechaFin") LocalDate fechaFin,
                                   @Param("pacienteId") Long pacienteId,
                                   @Param("medicoId") Long medicoId);
}
