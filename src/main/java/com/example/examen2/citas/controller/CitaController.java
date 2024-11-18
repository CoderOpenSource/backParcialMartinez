package com.example.examen2.citas.controller;

import com.example.examen2.citas.model.CitaDTO;
import com.example.examen2.citas.model.CitaRequest;
import com.example.examen2.citas.service.CitaService;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.medicos.service.MedicoService;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.paciente.service.PacienteService;
import com.example.examen2.especialidades.model.Especialidad;
import com.example.examen2.especialidades.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private EspecialidadService especialidadService;

    @PostMapping("/agendar")
    public CitaDTO agendarCita(@RequestBody CitaRequest citaRequest) {
        Medico medico = medicoService.findById(citaRequest.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + citaRequest.getMedicoId()));
        Paciente paciente = pacienteService.findById(citaRequest.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + citaRequest.getPacienteId()));
        Especialidad especialidad = especialidadService.getEspecialidadById(citaRequest.getEspecialidadId())
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con ID: " + citaRequest.getEspecialidadId()));

        return citaService.agendarCita(citaRequest.getFecha(), citaRequest.getHora(), medico, paciente, especialidad)
                .map(citaService::convertirACitaDTO)
                .orElseThrow(() -> new RuntimeException("No se pudo agendar la cita. Verifique los datos e intente nuevamente."));
    }

    @GetMapping
    public List<CitaDTO> obtenerCitas() {
        return citaService.obtenerCitas();
    }

    @GetMapping("/horas-disponibles")
    public List<LocalTime> obtenerHorasDisponibles(@RequestParam LocalDate fecha, @RequestParam Long medicoId) {
        Medico medico = medicoService.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + medicoId));
        return citaService.obtenerHorasDisponibles(fecha, medico);
    }

    @DeleteMapping("/{id}")
    public void eliminarCita(@PathVariable Long id) {
        citaService.eliminarCita(id);
    }
    // Nuevo método para actualizar la cita
    @PutMapping("/{id}")
    public CitaDTO actualizarCita(@PathVariable Long id, @RequestBody CitaDTO citaDTO) {
        return citaService.actualizarCita(id, citaDTO)
                .map(citaService::convertirACitaDTO)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
    }

}
