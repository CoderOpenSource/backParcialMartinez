package com.example.examen2.especialidades.controller;

import com.example.examen2.especialidades.model.Especialidad;
import com.example.examen2.especialidades.service.EspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    // Obtener todas las especialidades
    @GetMapping
    public List<Especialidad> getAllEspecialidades() {
        return especialidadService.getAllEspecialidades();
    }

    // Obtener una especialidad por ID
    @GetMapping("/{id}")
    public ResponseEntity<Especialidad> getEspecialidadById(@PathVariable Long id) {
        return especialidadService.getEspecialidadById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear una nueva especialidad
    @PostMapping
    public ResponseEntity<Especialidad> createEspecialidad(@RequestBody Especialidad especialidad) {
        Especialidad createdEspecialidad = especialidadService.saveEspecialidad(especialidad);
        return ResponseEntity.ok(createdEspecialidad);
    }

    // Actualizar una especialidad existente
    @PutMapping("/{id}")
    public ResponseEntity<Especialidad> updateEspecialidad(@PathVariable Long id, @RequestBody Especialidad especialidad) {
        return especialidadService.getEspecialidadById(id)
                .map(existingEspecialidad -> {
                    existingEspecialidad.setNombre(especialidad.getNombre());
                    existingEspecialidad.setDescripcion(especialidad.getDescripcion());
                    return ResponseEntity.ok(especialidadService.saveEspecialidad(existingEspecialidad));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar una especialidad
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEspecialidad(@PathVariable Long id) {
        if (especialidadService.getEspecialidadById(id).isPresent()) {
            especialidadService.deleteEspecialidad(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
