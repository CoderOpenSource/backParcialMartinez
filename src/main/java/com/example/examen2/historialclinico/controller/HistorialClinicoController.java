package com.example.examen2.historialclinico.controller;

import com.example.examen2.historialclinico.model.HistorialClinicoDTO;
import com.example.examen2.historialclinico.service.HistorialClinicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historiales-clinicos")
public class HistorialClinicoController {

    @Autowired
    private HistorialClinicoService historialClinicoService;

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<HistorialClinicoDTO> obtenerHistorialPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(historialClinicoService.obtenerHistorialPorPaciente(pacienteId));
    }
}
