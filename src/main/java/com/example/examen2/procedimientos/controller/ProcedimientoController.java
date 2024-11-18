package com.example.examen2.procedimientos.controller;

import com.example.examen2.procedimientos.model.ProcedimientoDTO;
import com.example.examen2.procedimientos.service.ProcedimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/procedimientos")
public class ProcedimientoController {

    @Autowired
    private ProcedimientoService procedimientoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProcedimientoDTO> crearProcedimiento(
            @RequestParam("medicoId") Long medicoId,
            @RequestParam("pacienteId") Long pacienteId,
            @RequestParam("consultaId") Long consultaId,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("observaciones") String observaciones,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo) {

        ProcedimientoDTO procedimientoDTO = procedimientoService.crearProcedimiento(
                medicoId, pacienteId, consultaId, descripcion, observaciones, archivo);

        return ResponseEntity.ok(procedimientoDTO);
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<ProcedimientoDTO>> obtenerPorMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(procedimientoService.obtenerProcedimientosPorMedico(medicoId));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ProcedimientoDTO>> obtenerPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(procedimientoService.obtenerProcedimientosPorPaciente(pacienteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProcedimiento(@PathVariable Long id) {
        procedimientoService.eliminarProcedimiento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProcedimientoDTO>> obtenerTodosLosProcedimientos() {
        return ResponseEntity.ok(procedimientoService.obtenerTodosLosProcedimientos());
    }
}

