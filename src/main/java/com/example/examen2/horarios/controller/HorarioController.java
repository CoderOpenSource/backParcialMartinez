package com.example.examen2.horarios.controller;

import com.example.examen2.horarios.model.HorarioDTO;
import com.example.examen2.horarios.service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @GetMapping("/medico/{medicoId}")
    public List<HorarioDTO> obtenerHorariosPorMedico(@PathVariable Long medicoId) {
        return horarioService.obtenerHorariosDTOPorMedico(medicoId);
    }

    @PostMapping("/medico/{medicoId}")
    public HorarioDTO crearHorario(@PathVariable Long medicoId, @RequestBody HorarioDTO horarioDTO) {
        return horarioService.crearHorario(medicoId, horarioDTO);
    }

    @PutMapping("/{horarioId}")
    public HorarioDTO actualizarHorario(@PathVariable Long horarioId, @RequestBody HorarioDTO horarioDTOActualizado) {
        return horarioService.actualizarHorario(horarioId, horarioDTOActualizado);
    }

    @DeleteMapping("/{horarioId}")
    public void eliminarHorario(@PathVariable Long horarioId) {
        horarioService.eliminarHorario(horarioId);
    }
}
