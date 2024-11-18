package com.example.examen2.consultas.repository;

import com.example.examen2.consultas.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByMedicoId(Long medicoId);
    List<Consulta> findByPacienteId(Long pacienteId);
    @Query("SELECT c FROM Consulta c " +
            "WHERE (:fechaInicio IS NULL OR c.fecha >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR c.fecha <= :fechaFin) " +
            "AND (:pacienteId IS NULL OR c.paciente.id = :pacienteId) " +
            "AND (:medicoId IS NULL OR c.medico.id = :medicoId) " +
            "AND (:especialidadId IS NULL OR c.cita.especialidad = :especialidadId)")
    List<Consulta> filtrarConsultas(@Param("fechaInicio") LocalDate fechaInicio,
                                    @Param("fechaFin") LocalDate fechaFin,
                                    @Param("pacienteId") Long pacienteId,
                                    @Param("medicoId") Long medicoId,
                                    @Param("especialidadId") Long especialidadId);
}
