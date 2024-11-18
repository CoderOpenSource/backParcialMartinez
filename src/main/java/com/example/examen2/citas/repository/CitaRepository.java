package com.example.examen2.citas.repository;

import com.example.examen2.citas.model.Cita;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.paciente.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByMedicoAndFechaAndHora(Medico medico, LocalDate fecha, LocalTime hora);

    // Nuevo método para verificar citas de un paciente en una fecha específica
    List<Cita> findByPacienteAndFecha(Paciente paciente, LocalDate fecha);
    // Nuevo método para obtener todas las citas de un médico en una fecha específica
    List<Cita> findByMedicoAndFecha(Medico medico, LocalDate fecha);
}
