package com.example.examen2.paciente.controller;

import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.paciente.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import java.util.Map;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // Listar todos los pacientes
    @GetMapping
    public List<Paciente> getAllPacientes() {
        return pacienteService.getAllPacientes();
    }



    @PostMapping("/cargar")
    public ResponseEntity<Map<String, String>> cargarExcel(@RequestParam("file") MultipartFile file) {
        try {
            pacienteService.cargarExcel(file);
            // Cambiar la respuesta a un JSON en lugar de texto plano
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Archivo cargado y procesado exitosamente"));
        } catch (IOException e) {
            // Devolver un mensaje de error en formato JSON en caso de excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", "Error al procesar el archivo: " + e.getMessage()));
        }
    }

}
