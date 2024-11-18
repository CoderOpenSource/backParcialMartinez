package com.example.examen2.analisis.service;

import com.example.examen2.analisis.model.AnalisisDTO;
import com.example.examen2.analisis.model.Analisis;
import com.example.examen2.analisis.repository.AnalisisRepository;
import com.example.examen2.config.cloudinary.CloudinaryService;
import com.example.examen2.historialclinico.service.HistorialClinicoService;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.medicos.repository.MedicoRepository;
import com.example.examen2.notification.service.NotificationService;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.paciente.repository.PacienteRepository;
import com.example.examen2.paciente.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AnalisisService {

    @Autowired
    private AnalisisRepository analisisRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private HistorialClinicoService historialClinicoService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    public AnalisisDTO crearAnalisis(Long pacienteId, Long medicoId, String tipoAnalisis,
                                     String resultado, String fechaRealizacion, MultipartFile archivo) {
        // Verificar existencia de paciente y médico
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        // Subir archivo a Cloudinary
        String archivoUrl;
        try (InputStream inputStream = archivo.getInputStream()) {
            String contentType = archivo.getContentType();
            Map<String, Object> uploadResult = cloudinaryService.uploadArchivos(inputStream, contentType);
            archivoUrl = uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al subir el archivo", e);
        }

        // Crear y guardar el análisis
        Analisis analisis = new Analisis();
        analisis.setPaciente(paciente);
        analisis.setMedico(medico);
        analisis.setTipoAnalisis(tipoAnalisis);
        analisis.setResultado(resultado);
        analisis.setFechaRealizacion(LocalDate.parse(fechaRealizacion));
        analisis.setArchivoUrl(archivoUrl);

        analisis = analisisRepository.save(analisis);

        // Actualizar historial clínico
        historialClinicoService.actualizarHistorialClinico(paciente);

        // Notificar al paciente que los resultados están disponibles
        notificarPacienteResultados(paciente, tipoAnalisis, archivoUrl);

        return convertirAAnalisisDTO(analisis);
    }

    private void notificarPacienteResultados(Paciente paciente, String tipoAnalisis, String archivoUrl) {
        String correoPaciente = paciente.getUsuario().getEmail();
        String tokenPaciente = paciente.getUsuario().getFcmToken();

        // Notificar por correo
        if (correoPaciente != null) {
            String asunto = "Resultados de su análisis médico";
            String cuerpo = String.format(
                    "Estimado %s %s,\n\nLos resultados de su análisis de tipo '%s' ya están disponibles.\n\n" +
                            "Puede revisarlos accediendo al siguiente enlace:\n%s\n\nGracias,\nEquipo Médico",
                    paciente.getUsuario().getNombre(),
                    paciente.getUsuario().getApellido(),
                    tipoAnalisis,
                    archivoUrl
            );
            emailService.enviarCorreo(correoPaciente, asunto, cuerpo);
        }

        // Notificar por push notification
        if (tokenPaciente != null) {
            String titulo = "Resultados de análisis disponibles";
            String mensaje = String.format(
                    "Los resultados de su análisis de tipo '%s' ya están disponibles. Puede revisarlos en el sistema.",
                    tipoAnalisis
            );
            notificationService.sendNotification(tokenPaciente, titulo, mensaje);
        }
    }


    public List<AnalisisDTO> obtenerAnalisisPorPaciente(Long pacienteId) {
        return analisisRepository.findByPacienteId(pacienteId).stream().map(this::convertirAAnalisisDTO).toList();
    }

    public List<AnalisisDTO> obtenerAnalisisPorMedico(Long medicoId) {
        return analisisRepository.findByMedicoId(medicoId).stream().map(this::convertirAAnalisisDTO).toList();
    }

    private AnalisisDTO convertirAAnalisisDTO(Analisis analisis) {
        AnalisisDTO dto = new AnalisisDTO();
        dto.setId(analisis.getId());
        dto.setPacienteId(analisis.getPaciente().getId());
        dto.setPacienteNombre(analisis.getPaciente().getUsuario().getNombre());
        dto.setPacienteApellido(analisis.getPaciente().getUsuario().getApellido());
        dto.setMedicoId(analisis.getMedico().getId());
        dto.setMedicoNombre(analisis.getMedico().getUsuario().getNombre());
        dto.setMedicoApellido(analisis.getMedico().getUsuario().getApellido());
        dto.setTipoAnalisis(analisis.getTipoAnalisis());
        dto.setResultado(analisis.getResultado());
        dto.setFechaRealizacion(analisis.getFechaRealizacion());
        dto.setArchivoUrl(analisis.getArchivoUrl());
        return dto;
    }
    public List<AnalisisDTO> obtenerTodosLosAnalisis() {
        return analisisRepository.findAll().stream()
                .map(this::convertirAAnalisisDTO)
                .toList();
    }
}
