package com.example.examen2.historialclinico.service;

import com.example.examen2.analisis.model.Analisis;
import com.example.examen2.analisis.repository.AnalisisRepository;
import com.example.examen2.consultas.model.Consulta;
import com.example.examen2.consultas.repository.ConsultaRepository;
import com.example.examen2.historialclinico.model.HistorialClinico;
import com.example.examen2.historialclinico.model.HistorialClinicoDTO;
import com.example.examen2.historialclinico.repository.HistorialClinicoRepository;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.procedimientos.model.Procedimiento;
import com.example.examen2.paciente.repository.PacienteRepository;
import com.example.examen2.paciente.service.PdfGenerator;
import com.example.examen2.config.cloudinary.CloudinaryService;
import com.example.examen2.procedimientos.repository.ProcedimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class HistorialClinicoService {

    @Autowired
    private HistorialClinicoRepository historialClinicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PdfGenerator pdfGenerator;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ProcedimientoRepository procedimientoRepository;

    @Autowired
    private AnalisisRepository analisisRepository;

    public HistorialClinico crearHistorialClinico(Paciente paciente) {
        HistorialClinico historial = new HistorialClinico();
        historial.setPaciente(paciente);
        historial.setFechaCreacion(LocalDate.now());
        historial.setFechaUltimaActualizacion(LocalDate.now());
        historial.setNotas("");
        historial.setArchivoUrl(null);
        return historialClinicoRepository.save(historial);
    }

    public void actualizarHistorialClinico(Paciente paciente) {
        try {
            // Obtener las consultas asociadas al paciente
            List<Consulta> consultas = consultaRepository.findByPacienteId(paciente.getId());

            // Obtener los procedimientos asociados al paciente
            List<Procedimiento> procedimientos = procedimientoRepository.findByPacienteId(paciente.getId());

            // Obtener los análisis asociados al paciente
            List<Analisis> analisis = analisisRepository.findByPacienteId(paciente.getId());

            // Generar el PDF a partir de los datos obtenidos
            ByteArrayInputStream pdfStream = pdfGenerator.generateHistorialClinicoPdf(paciente, consultas, procedimientos, analisis);

            // Subir el PDF a Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadArchivos(pdfStream, "application/pdf");
            String archivoUrl = uploadResult.get("secure_url").toString();

            // Buscar el historial clínico asociado al paciente
            HistorialClinico historialClinico = historialClinicoRepository.findByPacienteId(paciente.getId())
                    .orElseThrow(() -> new RuntimeException("No existe un historial clínico para el paciente con ID: " + paciente.getId()));

            // Actualizar los datos del historial clínico
            historialClinico.setArchivoUrl(archivoUrl); // Actualizar la URL del archivo
            historialClinico.setFechaUltimaActualizacion(LocalDate.now()); // Actualizar la fecha de última modificación
            historialClinicoRepository.save(historialClinico); // Guardar los cambios

        } catch (IOException e) {
            // Manejo de excepciones para errores en la generación o subida del PDF
            throw new RuntimeException("Error al generar o subir el PDF del historial clínico", e);
        }
    }



    public HistorialClinicoDTO obtenerHistorialPorPaciente(Long pacienteId) {
        HistorialClinico historial = historialClinicoRepository.findByPacienteId(pacienteId)
                .orElseThrow(() -> new RuntimeException("Historial clínico no encontrado para el paciente"));

        return convertirADTO(historial);
    }

    private HistorialClinicoDTO convertirADTO(HistorialClinico historial) {
        HistorialClinicoDTO dto = new HistorialClinicoDTO();
        dto.setId(historial.getId());
        dto.setPacienteId(historial.getPaciente().getId());
        dto.setPacienteNombre(historial.getPaciente().getUsuario().getNombre() + " " +
                historial.getPaciente().getUsuario().getApellido());
        dto.setFechaCreacion(historial.getFechaCreacion());
        dto.setFechaUltimaActualizacion(historial.getFechaUltimaActualizacion());
        dto.setNotas(historial.getNotas());
        dto.setArchivoUrl(historial.getArchivoUrl());
        return dto;
    }
}
