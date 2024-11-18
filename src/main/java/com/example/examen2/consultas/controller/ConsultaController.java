package com.example.examen2.consultas.controller;

import com.example.examen2.consultas.model.Consulta;
import com.example.examen2.consultas.model.ConsultaDTO;
import com.example.examen2.consultas.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @PostMapping
    public Consulta crearConsulta(@RequestBody ConsultaDTO consultaDTO) {
        return consultaService.crearConsulta(consultaDTO);
    }

    @GetMapping("/medico/{medicoId}")
    public List<ConsultaDTO> obtenerConsultasMedico(@PathVariable Long medicoId) {
        return consultaService.obtenerConsultasPorMedico(medicoId);
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<ConsultaDTO> obtenerConsultasPaciente(@PathVariable Long pacienteId) {
        return consultaService.obtenerConsultasPorPaciente(pacienteId);
    }

    @DeleteMapping("/{id}")
    public void eliminarConsulta(@PathVariable Long id) {
        consultaService.eliminarConsulta(id);
    }

    @GetMapping
    public List<ConsultaDTO> obtenerTodasLasConsultas() {
        return consultaService.obtenerTodasLasConsultas();
    }
}
