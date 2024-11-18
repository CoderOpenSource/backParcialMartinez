package com.example.examen2.reportes.controller;

import com.example.examen2.reportes.model.Reporte;
import com.example.examen2.reportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping
    public ResponseEntity<?> crearReporte(@RequestBody Map<String, Object> parametros) {
        try {
            String tipo = parametros.get("tipo").toString();
            String formato = parametros.get("formato").toString();
            Long pacienteId = parametros.containsKey("pacienteId") ? Long.valueOf(parametros.get("pacienteId").toString()) : null;
            Long medicoId = parametros.containsKey("medicoId") ? Long.valueOf(parametros.get("medicoId").toString()) : null;
            Long especialidadId = parametros.containsKey("especialidadId") ? Long.valueOf(parametros.get("especialidadId").toString()) : null;
            Date fechaInicio = parametros.containsKey("fechaInicio") ? new Date(parametros.get("fechaInicio").toString()) : null;
            Date fechaFin = parametros.containsKey("fechaFin") ? new Date(parametros.get("fechaFin").toString()) : null;

            Reporte reporte = reporteService.generarYSubirReporte(tipo, formato, pacienteId, medicoId, especialidadId, fechaInicio, fechaFin);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte: " + e.getMessage());
        }
    }
    // Método para obtener todos los reportes
    @GetMapping
    public ResponseEntity<List<Reporte>> obtenerTodosLosReportes() {
        List<Reporte> reportes = reporteService.obtenerTodosLosReportes();
        return ResponseEntity.ok(reportes);
    }

    // Método para eliminar un reporte por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReporte(@PathVariable Long id) {
        try {
            reporteService.eliminarReportePorId(id);
            return ResponseEntity.ok("Reporte eliminado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el reporte: " + e.getMessage());
        }
    }

}
