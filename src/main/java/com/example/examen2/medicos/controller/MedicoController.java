package com.example.examen2.medicos.controller;

import com.example.examen2.medicos.model.Medico;
import com.example.examen2.medicos.model.MedicoDTO;
import com.example.examen2.medicos.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    // Crear un nuevo médico
    @PostMapping
    public ResponseEntity<Medico> createMedico(@RequestBody MedicoDTO medicoDTO) {
        Medico savedMedico = medicoService.createMedico(medicoDTO);
        return ResponseEntity.ok(savedMedico);
    }

    // Listar todos los médicos, devolviendo solo MedicoDTO
    @GetMapping
    public List<MedicoDTO> getAllMedicos() {
        return medicoService.getAllMedicosDTO();
    }

    // Obtener un médico por ID, devolviendo solo MedicoDTO
    @GetMapping("/{id}")
    public ResponseEntity<MedicoDTO> getMedicoById(@PathVariable Long id) {
        return medicoService.getMedicoDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoDTO> updateMedico(@PathVariable Long id, @RequestBody MedicoDTO medicoDTO) {
        try {
            Medico updatedMedico = medicoService.updateMedico(id, medicoDTO);

            // Usar la función de conversión para devolver un MedicoDTO
            MedicoDTO responseDto = medicoService.convertToMedicoDTO(updatedMedico);

            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }



    // Eliminar un médico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedico(@PathVariable Long id) {
        medicoService.deleteMedico(id);
        return ResponseEntity.noContent().build();
    }
}
