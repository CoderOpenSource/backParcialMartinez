package com.example.examen2.horarios.service;

import com.example.examen2.horarios.model.Horario;
import com.example.examen2.horarios.model.HorarioDTO;
import com.example.examen2.horarios.repository.HorarioRepository;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.medicos.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public List<HorarioDTO> obtenerHorariosDTOPorMedico(Long medicoId) {
        System.out.println("==> Obteniendo horarios para el médico con ID: " + medicoId);
        List<HorarioDTO> horarios = horarioRepository.findByMedicoId(medicoId)
                .stream()
                .map(this::convertirAHorarioDTO)
                .collect(Collectors.toList());
        System.out.println("Horarios encontrados: " + horarios.size());
        return horarios;
    }

    public HorarioDTO crearHorario(Long medicoId, HorarioDTO horarioDTO) {
        System.out.println("==> Creando horario para el médico con ID: " + medicoId);
        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isPresent()) {
            System.out.println("Médico encontrado. Procediendo a crear el horario.");
            Horario horario = convertirAHorario(horarioDTO);
            horario.setMedico(medicoOpt.get());
            Horario horarioGuardado = horarioRepository.save(horario);
            System.out.println("Horario creado con éxito: ID = " + horarioGuardado.getId());
            return convertirAHorarioDTO(horarioGuardado);
        } else {
            System.out.println("Error: Médico no encontrado con ID: " + medicoId);
            throw new RuntimeException("Médico no encontrado con ID: " + medicoId);
        }
    }

    public HorarioDTO actualizarHorario(Long horarioId, HorarioDTO horarioDTOActualizado) {
        System.out.println("==> Actualizando horario con ID: " + horarioId);
        return horarioRepository.findById(horarioId).map(horario -> {
            horario.setDia(horarioDTOActualizado.getDia());
            horario.setHoraInicio(horarioDTOActualizado.getHoraInicio());
            horario.setHoraFin(horarioDTOActualizado.getHoraFin());
            Horario horarioActualizado = horarioRepository.save(horario);
            System.out.println("Horario actualizado con éxito: ID = " + horarioActualizado.getId());
            return convertirAHorarioDTO(horarioActualizado);
        }).orElseThrow(() -> {
            System.out.println("Error: Horario no encontrado con ID: " + horarioId);
            return new RuntimeException("Horario no encontrado con ID: " + horarioId);
        });
    }

    public void eliminarHorario(Long horarioId) {
        System.out.println("==> Eliminando horario con ID: " + horarioId);
        if (horarioRepository.existsById(horarioId)) {
            horarioRepository.deleteById(horarioId);
            System.out.println("Horario eliminado con éxito.");
        } else {
            System.out.println("Error: Horario no encontrado con ID: " + horarioId);
            throw new RuntimeException("Horario no encontrado con ID: " + horarioId);
        }
    }

    public Horario obtenerHorarioPorDia(Long medicoId, String dia) {
        System.out.println("==> Buscando horario para el médico con ID: " + medicoId + " y día: " + dia);
        Horario horario = horarioRepository.findByMedicoIdAndDia(medicoId, dia);
        if (horario != null) {
            System.out.println("Horario encontrado: " + horario.getHoraInicio() + " - " + horario.getHoraFin());
        } else {
            System.out.println("No se encontró horario para el médico con ID: " + medicoId + " y día: " + dia);
        }
        return horario;
    }

    // Métodos de conversión entre Horario y HorarioDTO
    private HorarioDTO convertirAHorarioDTO(Horario horario) {
        return new HorarioDTO(
                horario.getId(),
                horario.getDia(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getMedico().getId()
        );
    }

    private Horario convertirAHorario(HorarioDTO horarioDTO) {
        Horario horario = new Horario();
        horario.setId(horarioDTO.getId());
        horario.setDia(horarioDTO.getDia());
        horario.setHoraInicio(horarioDTO.getHoraInicio());
        horario.setHoraFin(horarioDTO.getHoraFin());
        return horario;
    }
}
