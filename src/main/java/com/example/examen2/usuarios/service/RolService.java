package com.example.examen2.usuarios.service;

import com.example.examen2.usuarios.model.Rol;
import com.example.examen2.usuarios.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    public Rol createRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol getRolById(Long id) {
        return rolRepository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    public Rol updateRol(Long id, Rol rolDetails) {
        Rol rol = rolRepository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        rol.setNombre(rolDetails.getNombre());

        return rolRepository.save(rol);
    }

    public void deleteRol(Long id) {
        Rol rol = rolRepository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        rolRepository.delete(rol);
    }
}
