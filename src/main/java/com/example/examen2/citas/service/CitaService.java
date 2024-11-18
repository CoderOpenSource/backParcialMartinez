package com.example.examen2.citas.service;

import com.example.examen2.citas.model.Cita;
import com.example.examen2.citas.model.CitaDTO;
import com.example.examen2.citas.repository.CitaRepository;
import com.example.examen2.horarios.model.Horario;
import com.example.examen2.horarios.service.HorarioService;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.notification.service.NotificationService;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.especialidades.model.Especialidad;
import com.example.examen2.paciente.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService; // Inyectamos el servicio de notificaciones

    // Método para enviar notificaciones push
    private void enviarNotificacionesDeCita(Cita cita) {
        // Notificación para el paciente
        String tokenPaciente = cita.getPaciente().getUsuario().getFcmToken();
        if (tokenPaciente != null) {
            String tituloPaciente = "Confirmación de cita médica";
            String mensajePaciente = String.format(
                    "Su cita con el Dr. %s %s ha sido confirmada para el %s a las %s.",
                    cita.getMedico().getUsuario().getNombre(),
                    cita.getMedico().getUsuario().getApellido(),
                    cita.getFecha(),
                    cita.getHora()
            );

            notificationService.sendNotification(tokenPaciente, tituloPaciente, mensajePaciente);
        }

        // Notificación para el médico
        String tokenMedico = cita.getMedico().getUsuario().getFcmToken();
        if (tokenMedico != null) {
            String tituloMedico = "Nueva cita médica asignada";
            String mensajeMedico = String.format(
                    "Tiene una cita programada con el paciente %s %s el %s a las %s.",
                    cita.getPaciente().getUsuario().getNombre(),
                    cita.getPaciente().getUsuario().getApellido(),
                    cita.getFecha(),
                    cita.getHora()
            );

            notificationService.sendNotification(tokenMedico, tituloMedico, mensajeMedico);
        }
    }

    private static final Map<String, String> dayTranslations = new HashMap<>();

    static {
        dayTranslations.put("MONDAY", "LUNES");
        dayTranslations.put("TUESDAY", "MARTES");
        dayTranslations.put("WEDNESDAY", "MIERCOLES");
        dayTranslations.put("THURSDAY", "JUEVES");
        dayTranslations.put("FRIDAY", "VIERNES");
        dayTranslations.put("SATURDAY", "SABADO");
        dayTranslations.put("SUNDAY", "DOMINGO");
    }
    private void enviarCorreosDeConfirmacion(Cita cita) {
        String correoPaciente = cita.getPaciente().getUsuario().getEmail();
        String correoMedico = cita.getMedico().getUsuario().getEmail();

        // Mensaje para el paciente
        String asuntoPaciente = "Confirmación de su cita médica";
        String cuerpoPaciente = String.format(
                "Estimado %s,\n\nSu cita ha sido confirmada con los siguientes detalles:\n\n" +
                        "Fecha: %s\nHora: %s\nMédico: %s %s\nEspecialidad: %s\n\n" +
                        "Por favor, asegúrese de llegar a tiempo.\n\nGracias,\nEquipo Médico",
                cita.getPaciente().getUsuario().getNombre(),
                cita.getFecha(),
                cita.getHora(),
                cita.getMedico().getUsuario().getNombre(),
                cita.getMedico().getUsuario().getApellido(),
                cita.getEspecialidad().getNombre()
        );

        emailService.enviarCorreo(correoPaciente, asuntoPaciente, cuerpoPaciente);

        // Mensaje para el médico
        String asuntoMedico = "Nueva cita confirmada";
        String cuerpoMedico = String.format(
                "Estimado Dr. %s,\n\nSe le ha asignado una nueva cita con los siguientes detalles:\n\n" +
                        "Fecha: %s\nHora: %s\nPaciente: %s %s\nEspecialidad: %s\n\n" +
                        "Por favor, prepare la consulta según los requerimientos.\n\nGracias,\nEquipo Administrativo",
                cita.getMedico().getUsuario().getApellido(),
                cita.getFecha(),
                cita.getHora(),
                cita.getPaciente().getUsuario().getNombre(),
                cita.getPaciente().getUsuario().getApellido(),
                cita.getEspecialidad().getNombre()
        );

        emailService.enviarCorreo(correoMedico, asuntoMedico, cuerpoMedico);

        System.out.println("Correos de confirmación enviados.");
    }

    public Optional<Cita> agendarCita(LocalDate fecha, LocalTime hora, Medico medico, Paciente paciente, Especialidad especialidad) {
        try {
            System.out.println("==> Iniciando agendarCita");
            System.out.println("Fecha: " + fecha);
            System.out.println("Hora: " + hora);
            System.out.println("Medico ID: " + medico.getId());
            System.out.println("Paciente ID: " + paciente.getId());
            System.out.println("Especialidad ID: " + especialidad.getId());

            // Validar si el seguro del paciente ha expirado
            if (paciente.getFechaFinSeguro().isBefore(LocalDate.now())) {
                System.out.println("Error: El seguro del paciente ha expirado.");
                throw new RuntimeException("Su seguro ha expirado.");
            }

            String diaEnEspañol = dayTranslations.get(fecha.getDayOfWeek().toString());
            System.out.println("Día de la semana (español): " + diaEnEspañol);

            Horario horario = horarioService.obtenerHorarioPorDia(medico.getId(), diaEnEspañol);
            System.out.println("Horario obtenido: " + (horario != null ? horario.getHoraInicio() + " - " + horario.getHoraFin() : "No se encontró horario"));

            if (horario == null || !hora.isAfter(horario.getHoraInicio()) || !hora.isBefore(horario.getHoraFin())) {
                System.out.println("Error: La hora de la cita no está dentro del horario del médico.");
                throw new RuntimeException("La hora de la cita no está dentro del horario del médico.");
            }

            // Validar si el paciente ya tiene una cita en ese día
            List<Cita> citasDelPaciente = citaRepository.findByPacienteAndFecha(paciente, fecha);
            System.out.println("Citas del paciente en la fecha " + fecha + ": " + citasDelPaciente.size());
            if (!citasDelPaciente.isEmpty()) {
                System.out.println("Error: El paciente ya tiene una cita programada para este día.");
                throw new RuntimeException("El paciente ya tiene una cita programada para este día.");
            }

            // Validar si el médico ya tiene una cita en esa fecha y hora
            List<Cita> citasExistentes = citaRepository.findByMedicoAndFechaAndHora(medico, fecha, hora);
            System.out.println("Citas del médico en la fecha y hora " + fecha + " " + hora + ": " + citasExistentes.size());
            if (!citasExistentes.isEmpty()) {
                System.out.println("Error: El médico ya tiene una cita a esta hora.");
                throw new RuntimeException("El médico ya tiene una cita a esta hora.");
            }

            System.out.println("Creando nueva cita...");
            Cita nuevaCita = new Cita(fecha, hora, medico, paciente, horario, especialidad, "aceptado");
            Cita citaGuardada = citaRepository.save(nuevaCita);
            System.out.println("Cita creada con éxito: ID = " + citaGuardada.getId());

            // Enviar correos
            enviarCorreosDeConfirmacion(citaGuardada);

            enviarNotificacionesDeCita(citaGuardada);

            return Optional.of(citaGuardada);

        } catch (Exception e) {
            System.err.println("Error al agendar cita: " + e.getMessage());
            return Optional.empty();
        }
    }




    // Genera una lista de horas disponibles en el horario del médico
    public List<LocalTime> obtenerHorasDisponibles(LocalDate fecha, Medico medico) {
        String diaEnEspañol = dayTranslations.get(fecha.getDayOfWeek().toString());
        Horario horario = horarioService.obtenerHorarioPorDia(medico.getId(), diaEnEspañol);

        if (horario == null) {
            return List.of(); // No hay horario disponible para este día
        }

        List<Cita> citasDelDia = citaRepository.findByMedicoAndFecha(medico, fecha);
        List<LocalTime> horasOcupadas = citasDelDia.stream().map(Cita::getHora).collect(Collectors.toList());

        List<LocalTime> horasDisponibles = new ArrayList<>();
        LocalTime horaActual = horario.getHoraInicio();

        while (horaActual.isBefore(horario.getHoraFin())) {
            if (!horasOcupadas.contains(horaActual)) {
                horasDisponibles.add(horaActual);
            }
            horaActual = horaActual.plusHours(1); // Intervalo de 1 hora entre citas
        }

        return horasDisponibles;
    }

    public CitaDTO convertirACitaDTO(Cita cita) {
        return new CitaDTO(
                cita.getId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getMedico().getId(),
                cita.getMedico().getUsuario().getNombre(),
                cita.getMedico().getUsuario().getApellido(),
                cita.getPaciente().getId(),
                cita.getPaciente().getUsuario().getNombre(),
                cita.getPaciente().getUsuario().getApellido(),
                cita.getHorario().getDia(),
                cita.getHorario().getHoraInicio(),
                cita.getHorario().getHoraFin(),
                cita.getEspecialidad().getId(),
                cita.getEspecialidad().getNombre(),
                cita.getEstado(),
                cita.isConsultaCreada(), // Agregar consultaCreada a DTO
                cita.getObservaciones()  // Agregar observaciones a DTO
        );
    }



    public List<CitaDTO> obtenerCitas() {
        return citaRepository.findAll().stream()
                .map(this::convertirACitaDTO)
                .collect(Collectors.toList());
    }
    public void eliminarCita(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
        citaRepository.deleteById(id);
    }
    public Optional<Cita> actualizarCita(Long id, CitaDTO citaDTO) {
        return citaRepository.findById(id).map(cita -> {
            cita.setFecha(citaDTO.getFecha());
            cita.setHora(citaDTO.getHora());
            cita.setEstado(citaDTO.getEstado());
            cita.setConsultaCreada(citaDTO.isConsultaCreada());
            return citaRepository.save(cita);
        });
    }

}
