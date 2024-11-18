package com.example.examen2.procedimientos.repository;

import com.example.examen2.procedimientos.model.Procedimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProcedimientoRepository extends JpaRepository<Procedimiento, Long> {
    List<Procedimiento> findByMedicoId(Long medicoId);
    List<Procedimiento> findByPacienteId(Long pacienteId);

    @Query("SELECT p FROM Procedimiento p " +
            "WHERE (:fechaInicio IS NULL OR p.fecha >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR p.fecha <= :fechaFin) " +
            "AND (:pacienteId IS NULL OR p.paciente.id = :pacienteId) " +
            "AND (:medicoId IS NULL OR p.medico.id = :medicoId) " +
            "AND (:especialidadId IS NULL OR p.consulta.cita.especialidad.id = :especialidadId)")
    List<Procedimiento> filtrarProcedimientos(@Param("fechaInicio") LocalDate fechaInicio,
                                              @Param("fechaFin") LocalDate fechaFin,
                                              @Param("pacienteId") Long pacienteId,
                                              @Param("medicoId") Long medicoId,
                                              @Param("especialidadId") Long especialidadId);

}

