package com.example.examen2.horarios.repository;

import com.example.examen2.horarios.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByMedicoId(Long medicoId);
    Horario findByMedicoIdAndDia(Long medicoId, String dia);
}
