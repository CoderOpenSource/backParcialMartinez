package com.example.examen2.usuarios.service;

import com.example.examen2.usuarios.model.Usuario;
import com.example.examen2.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .filter(usuario -> !usuario.getRol().getNombre().equalsIgnoreCase("DOCENTE"))
                .collect(Collectors.toList());
    }

    public Usuario createUsuario(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    public Usuario getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (usuario.getRol().getNombre().equalsIgnoreCase("DOCENTE")) {
            throw new RuntimeException("No se puede obtener un usuario con rol DOCENTE");
        }
        return usuario;
    }

    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(usuarioDetails.getNombre());
        usuario.setApellido(usuarioDetails.getApellido());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setContrasena(usuarioDetails.getContrasena());

        return usuarioRepository.save(usuario);
    }

    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }
    // Método para actualizar el FCM Token
    public void updateFcmToken(Long id, String fcmToken) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizamos el FCM Token
        usuario.setFcmToken(fcmToken);
        usuarioRepository.save(usuario);
    }
}
