package com.example.examen2.consultas.service;

import com.example.examen2.consultas.model.ConsultaDTO;
import com.example.examen2.consultas.model.Consulta;
import com.example.examen2.consultas.repository.ConsultaRepository;
import com.example.examen2.citas.model.Cita;
import com.example.examen2.citas.repository.CitaRepository;
import com.example.examen2.historialclinico.service.HistorialClinicoService;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.medicos.repository.MedicoRepository;
import com.example.examen2.notification.service.NotificationService;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.paciente.repository.PacienteRepository;
import com.example.examen2.paciente.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private HistorialClinicoService historialClinicoService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    public Consulta crearConsulta(ConsultaDTO consultaDTO) {
        // Buscar las entidades relacionadas
        Optional<Cita> citaOpt = citaRepository.findById(consultaDTO.getCitaId());
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(consultaDTO.getPacienteId());
        Optional<Medico> medicoOpt = medicoRepository.findById(consultaDTO.getMedicoId());

        if (citaOpt.isPresent() && pacienteOpt.isPresent() && medicoOpt.isPresent()) {
            Cita cita = citaOpt.get();
            Paciente paciente = pacienteOpt.get();

            // Verificar si ya existe una consulta creada para la cita
            if (cita.isConsultaCreada()) {
                throw new RuntimeException("La consulta ya fue creada para esta cita");
            }

            // Crear la consulta
            Consulta consulta = new Consulta();
            consulta.setCita(cita);
            consulta.setPaciente(paciente);
            consulta.setMedico(medicoOpt.get());
            consulta.setFecha(LocalDate.now());
            consulta.setDiagnostico(consultaDTO.getDiagnostico());
            consulta.setSintomas(consultaDTO.getSintomas());
            consulta.setTratamiento(consultaDTO.getTratamiento());
            consulta.setNotas(consultaDTO.getNotas());
            consulta.setDerivoProcedimiento(consultaDTO.getDerivoProcedimiento());

            // Guardar la consulta
            Consulta nuevaConsulta = consultaRepository.save(consulta);

            // Actualizar el estado de la cita
            cita.setConsultaCreada(true);
            citaRepository.save(cita);

            // Actualizar el historial clínico del paciente
            historialClinicoService.actualizarHistorialClinico(paciente);

            // Notificar al paciente
            notificarPacienteConsultaCreada(paciente, cita, consulta);

            return nuevaConsulta;
        } else {
            throw new RuntimeException("Cita, Paciente o Médico no encontrado");
        }
    }

    private void notificarPacienteConsultaCreada(Paciente paciente, Cita cita, Consulta consulta) {
        String correoPaciente = paciente.getUsuario().getEmail();
        String tokenPaciente = paciente.getUsuario().getFcmToken();

        // Notificación por correo
        if (correoPaciente != null) {
            String asunto = "Resultados de su consulta médica";
            String cuerpo = String.format(
                    "Estimado %s %s,\n\nSe ha generado una consulta médica con los siguientes detalles:\n\n" +
                            "Fecha de la consulta: %s\n" +
                            "Médico: %s %s\n" +
                            "Diagnóstico: %s\n" +
                            "Tratamiento: %s\n\n" +
                            "Para más información, consulte su historial médico en nuestro sistema.\n\n" +
                            "Gracias,\nEquipo Médico",
                    paciente.getUsuario().getNombre(),
                    paciente.getUsuario().getApellido(),
                    consulta.getFecha(),
                    cita.getMedico().getUsuario().getNombre(),
                    cita.getMedico().getUsuario().getApellido(),
                    consulta.getDiagnostico(),
                    consulta.getTratamiento()
            );

            emailService.enviarCorreo(correoPaciente, asunto, cuerpo);
        }

        // Notificación push
        if (tokenPaciente != null) {
            String titulo = "Consulta médica creada";
            String mensaje = String.format(
                    "Se ha creado una consulta médica con el Dr. %s %s. Diagnóstico: %s.",
                    cita.getMedico().getUsuario().getNombre(),
                    cita.getMedico().getUsuario().getApellido(),
                    consulta.getDiagnostico()
            );

            notificationService.sendNotification(tokenPaciente, titulo, mensaje);
        }
    }



    public List<ConsultaDTO> obtenerConsultasPorMedico(Long medicoId) {
        List<Consulta> consultas = consultaRepository.findByMedicoId(medicoId);
        return consultas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<ConsultaDTO> obtenerConsultasPorPaciente(Long pacienteId) {
        List<Consulta> consultas = consultaRepository.findByPacienteId(pacienteId);
        return consultas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public void eliminarConsulta(Long id) {
        consultaRepository.deleteById(id);
    }

    private ConsultaDTO convertirADTO(Consulta consulta) {
        ConsultaDTO dto = new ConsultaDTO();
        dto.setId(consulta.getId());
        dto.setCitaId(consulta.getCita().getId());
        dto.setPacienteId(consulta.getPaciente().getId());
        dto.setPacienteNombre(consulta.getPaciente().getUsuario().getNombre() + " " +
                consulta.getPaciente().getUsuario().getApellido());
        dto.setMedicoId(consulta.getMedico().getId());
        dto.setNombreMedico(consulta.getMedico().getUsuario().getNombre() + " " + consulta.getMedico().getUsuario().getApellido()); // Nuevo
        dto.setFecha(consulta.getFecha());
        dto.setDiagnostico(consulta.getDiagnostico());
        dto.setSintomas(consulta.getSintomas());
        dto.setTratamiento(consulta.getTratamiento());
        dto.setNotas(consulta.getNotas());
        dto.setDerivoProcedimiento(consulta.getDerivoProcedimiento());

        // Incluir datos de la especialidad a partir de la cita
        if (consulta.getCita().getEspecialidad() != null) {
            dto.setEspecialidadId(consulta.getCita().getEspecialidad().getId());
            dto.setEspecialidadNombre(consulta.getCita().getEspecialidad().getNombre());
        }

        return dto;
    }


    public List<ConsultaDTO> obtenerTodasLasConsultas() {
        return consultaRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }


}
