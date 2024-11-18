package com.example.examen2.especialidades.service;

import com.example.examen2.especialidades.model.Especialidad;
import com.example.examen2.especialidades.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public List<Especialidad> getAllEspecialidades() {
        return especialidadRepository.findAll();
    }

    public Optional<Especialidad> getEspecialidadById(Long id) {
        return especialidadRepository.findById(id);
    }

    public Especialidad saveEspecialidad(Especialidad especialidad) {
        return especialidadRepository.save(especialidad);
    }

    public void deleteEspecialidad(Long id) {
        especialidadRepository.deleteById(id);
    }
}
