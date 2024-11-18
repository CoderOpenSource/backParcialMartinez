package com.example.examen2.citas.service;

import com.example.examen2.citas.model.Cita;
import com.example.examen2.citas.repository.CitaRepository;
import com.example.examen2.notification.service.NotificationService;
import com.example.examen2.paciente.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitaCancelacionService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService; // Inyectamos el servicio de notificaciones

    public boolean cancelarCitaPorPaciente(Long citaId, Long pacienteId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + citaId));

        if (!cita.getPaciente().getId().equals(pacienteId)) {
            throw new RuntimeException("El paciente no está autorizado para cancelar esta cita.");
        }

        cita.setEstado("cancelada");
        citaRepository.save(cita);

        String asunto = "Cita Cancelada por Paciente";
        String cuerpo = "El paciente " + cita.getPaciente().getUsuario().getNombre() +
                " " + cita.getPaciente().getUsuario().getApellido() +
                " ha cancelado la cita programada para el " + cita.getFecha() +
                " a las " + cita.getHora() + ".";

        emailService.enviarCorreo(
                cita.getMedico().getUsuario().getEmail(),
                asunto,
                cuerpo
        );

        // Notificación al médico
        String tokenMedico = cita.getMedico().getUsuario().getFcmToken();
        if (tokenMedico != null) {
            String tituloNotificacion = "Cita cancelada";
            String mensajeNotificacion = "El paciente " + cita.getPaciente().getUsuario().getNombre() +
                    " ha cancelado la cita programada para el " + cita.getFecha() +
                    " a las " + cita.getHora() + ".";
            notificationService.sendNotification(tokenMedico, tituloNotificacion, mensajeNotificacion);
        }

        return true;
    }

    public boolean cancelarCitaPorMedico(Long citaId, Long medicoId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + citaId));

        if (!cita.getMedico().getId().equals(medicoId)) {
            throw new RuntimeException("El médico no está autorizado para cancelar esta cita.");
        }

        cita.setEstado("cancelada");
        citaRepository.save(cita);

        String asunto = "Cita Cancelada por Médico";
        String cuerpo = "El médico " + cita.getMedico().getUsuario().getNombre() +
                " " + cita.getMedico().getUsuario().getApellido() +
                " ha cancelado la cita programada para el " + cita.getFecha() +
                " a las " + cita.getHora() + ".";

        emailService.enviarCorreo(
                cita.getPaciente().getUsuario().getEmail(),
                asunto,
                cuerpo
        );

        // Notificación al paciente
        String tokenPaciente = cita.getPaciente().getUsuario().getFcmToken();
        if (tokenPaciente != null) {
            String tituloNotificacion = "Cita cancelada";
            String mensajeNotificacion = "El médico " + cita.getMedico().getUsuario().getNombre() +
                    " ha cancelado la cita programada para el " + cita.getFecha() +
                    " a las " + cita.getHora() + ".";
            notificationService.sendNotification(tokenPaciente, tituloNotificacion, mensajeNotificacion);
        }

        return true;
    }

    public boolean aceptarCita(Long citaId, Long medicoId, String observaciones) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + citaId));

        if (!cita.getMedico().getId().equals(medicoId)) {
            throw new RuntimeException("El médico no está autorizado para aceptar esta cita.");
        }

        cita.setEstado("atendida");
        cita.setObservaciones(observaciones); // Guardar las observaciones
        citaRepository.save(cita);

        String asunto = "Cita Atendida";
        String cuerpo = "La cita programada para el " + cita.getFecha() +
                " a las " + cita.getHora() +
                " ha sido marcada como atendida por el médico " + cita.getMedico().getUsuario().getNombre() +
                " " + cita.getMedico().getUsuario().getApellido() + "." +
                "\n\nObservaciones: " + observaciones;

        emailService.enviarCorreo(
                cita.getPaciente().getUsuario().getEmail(),
                asunto,
                cuerpo
        );

        // Notificación al paciente
        String tokenPaciente = cita.getPaciente().getUsuario().getFcmToken();
        if (tokenPaciente != null) {
            String tituloNotificacion = "Cita Atendida";
            String mensajeNotificacion = "La cita programada para el " + cita.getFecha() +
                    " a las " + cita.getHora() +
                    " ha sido marcada como atendida por el médico " +
                    cita.getMedico().getUsuario().getNombre() + " " +
                    cita.getMedico().getUsuario().getApellido() +
                    ".\n\nObservaciones: " + (observaciones != null ? observaciones : "Sin observaciones.");
            notificationService.sendNotification(tokenPaciente, tituloNotificacion, mensajeNotificacion);
        }

        return true;
    }
}
