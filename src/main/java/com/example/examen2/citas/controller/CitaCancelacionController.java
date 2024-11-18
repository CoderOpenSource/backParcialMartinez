package com.example.examen2.citas.controller;

import com.example.examen2.citas.service.CitaCancelacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/citas")
public class CitaCancelacionController {

    @Autowired
    private CitaCancelacionService citaCancelacionService;

    // Cancelar cita por parte del paciente
    @PostMapping("/cancelar/paciente")
    public ResponseEntity<String> cancelarCitaPorPaciente(@RequestParam Long citaId, @RequestParam Long pacienteId) {
        boolean cancelada = citaCancelacionService.cancelarCitaPorPaciente(citaId, pacienteId);
        if (cancelada) {
            return ResponseEntity.ok("Cita cancelada correctamente por el paciente.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al cancelar la cita por el paciente.");
        }
    }

    // Cancelar cita por parte del médico
    @PostMapping("/cancelar/medico")
    public ResponseEntity<String> cancelarCitaPorMedico(@RequestParam Long citaId, @RequestParam Long medicoId) {
        boolean cancelada = citaCancelacionService.cancelarCitaPorMedico(citaId, medicoId);
        if (cancelada) {
            return ResponseEntity.ok("Cita cancelada correctamente por el médico.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al cancelar la cita por el médico.");
        }
    }


    @PostMapping("/aceptar")
    public ResponseEntity<String> aceptarCita(
            @RequestParam Long citaId,
            @RequestParam Long medicoId,
            @RequestParam String observaciones) { // Nuevo parámetro
        boolean aceptada = citaCancelacionService.aceptarCita(citaId, medicoId, observaciones);
        if (aceptada) {
            return ResponseEntity.ok("Cita marcada como atendida con observaciones.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al marcar la cita como atendida.");
        }
    }


}
