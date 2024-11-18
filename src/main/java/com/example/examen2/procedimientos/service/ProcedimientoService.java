package com.example.examen2.procedimientos.service;

import com.example.examen2.config.cloudinary.CloudinaryService;
import com.example.examen2.consultas.model.Consulta;
import com.example.examen2.consultas.repository.ConsultaRepository;
import com.example.examen2.historialclinico.service.HistorialClinicoService;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.medicos.repository.MedicoRepository;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.paciente.repository.PacienteRepository;
import com.example.examen2.procedimientos.model.Procedimiento;
import com.example.examen2.procedimientos.model.ProcedimientoDTO;
import com.example.examen2.procedimientos.repository.ProcedimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcedimientoService {

    @Autowired
    private ProcedimientoRepository procedimientoRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private HistorialClinicoService historialClinicoService;

    public ProcedimientoDTO crearProcedimiento(Long medicoId, Long pacienteId, Long consultaId, String descripcion,
                                               String observaciones, MultipartFile archivo) {
        // Verificar existencia de entidades relacionadas
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));

        // Subir archivo (si existe)
        String archivoUrl = null;
        if (archivo != null && !archivo.isEmpty()) {
            try (InputStream inputStream = archivo.getInputStream()) {
                String contentType = archivo.getContentType();
                Map<String, Object> uploadResult = cloudinaryService.uploadArchivos(inputStream, contentType);
                archivoUrl = uploadResult.get("secure_url").toString();
            } catch (IOException e) {
                throw new RuntimeException("Error al subir el archivo", e);
            }
        }

        // Crear y guardar procedimiento
        Procedimiento procedimiento = new Procedimiento(
                medico, paciente, consulta, descripcion, LocalDate.now(), observaciones, archivoUrl);
        procedimiento = procedimientoRepository.save(procedimiento);
        historialClinicoService.actualizarHistorialClinico(paciente);

        return convertirADTO(procedimiento);
    }

    public List<ProcedimientoDTO> obtenerProcedimientosPorMedico(Long medicoId) {
        List<Procedimiento> procedimientos = procedimientoRepository.findByMedicoId(medicoId);
        return procedimientos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<ProcedimientoDTO> obtenerProcedimientosPorPaciente(Long pacienteId) {
        List<Procedimiento> procedimientos = procedimientoRepository.findByPacienteId(pacienteId);
        return procedimientos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public void eliminarProcedimiento(Long id) {
        procedimientoRepository.deleteById(id);
    }

    private ProcedimientoDTO convertirADTO(Procedimiento procedimiento) {
        ProcedimientoDTO dto = new ProcedimientoDTO();
        dto.setId(procedimiento.getId());
        dto.setDescripcion(procedimiento.getDescripcion());
        dto.setFecha(procedimiento.getFecha());
        dto.setObservaciones(procedimiento.getObservaciones());
        dto.setArchivoUrl(procedimiento.getArchivoUrl());
        dto.setMedicoNombre(procedimiento.getMedico().getUsuario().getNombre());
        dto.setMedicoApellido(procedimiento.getMedico().getUsuario().getApellido());
        dto.setPacienteNombre(procedimiento.getPaciente().getUsuario().getNombre());
        dto.setPacienteApellido(procedimiento.getPaciente().getUsuario().getApellido());
        dto.setConsultaId(procedimiento.getConsulta().getId());
        return dto;
    }
    public List<ProcedimientoDTO> obtenerTodosLosProcedimientos() {
        List<Procedimiento> procedimientos = procedimientoRepository.findAll();
        return procedimientos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }
}

