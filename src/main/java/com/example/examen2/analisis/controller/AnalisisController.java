package com.example.examen2.analisis.controller;

import com.example.examen2.analisis.model.AnalisisDTO;
import com.example.examen2.analisis.service.AnalisisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/analisis")
public class AnalisisController {

    @Autowired
    private AnalisisService analisisService;


    @GetMapping("/paciente/{pacienteId}")
    public List<AnalisisDTO> obtenerAnalisisPorPaciente(@PathVariable Long pacienteId) {
        return analisisService.obtenerAnalisisPorPaciente(pacienteId);
    }

    @GetMapping("/medico/{medicoId}")
    public List<AnalisisDTO> obtenerAnalisisPorMedico(@PathVariable Long medicoId) {
        return analisisService.obtenerAnalisisPorMedico(medicoId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalisisDTO> crearAnalisis(
            @RequestParam("pacienteId") Long pacienteId,
            @RequestParam("medicoId") Long medicoId,
            @RequestParam("tipoAnalisis") String tipoAnalisis,
            @RequestParam("resultado") String resultado,
            @RequestParam("fechaRealizacion") String fechaRealizacion,
            @RequestParam("archivo") MultipartFile archivo) {

        // Llamar al servicio para crear el análisis
        AnalisisDTO analisisDTO = analisisService.crearAnalisis(
                pacienteId, medicoId, tipoAnalisis, resultado, fechaRealizacion, archivo);

        return ResponseEntity.ok(analisisDTO);
    }

    @GetMapping
    public List<AnalisisDTO> obtenerTodosLosAnalisis() {
        return analisisService.obtenerTodosLosAnalisis();
    }

}
