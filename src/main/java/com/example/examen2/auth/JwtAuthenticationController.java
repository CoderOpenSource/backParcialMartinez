package com.example.examen2.auth;

import com.example.examen2.config.jwt.JwtTokenUtil;
import com.example.examen2.usuarios.service.UsuarioDetailsServiceImpl;
import com.example.examen2.usuarios.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UsuarioDetailsServiceImpl userDetailsService;

    @PostMapping("/authenticate/")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        System.out.println("Attempting to authenticate user: " + authenticationRequest.getUsername());

        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            System.out.println("User authenticated: " + authenticationRequest.getUsername());
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        System.out.println("UserDetails loaded: " + userDetails.getUsername());

        Usuario usuario = userDetailsService.findByEmail(authenticationRequest.getUsername());
        if (usuario == null) {
            System.out.println("User not found in the database: " + authenticationRequest.getUsername());
            return ResponseEntity.status(404).body("User not found");
        }

        final String rol = usuario.getRol().getNombre();
        System.out.println("User role: " + rol);

        final Long id = usuario.getId();
        System.out.println("User ID: " + id);

        final String token = jwtTokenUtil.generateToken(userDetails, rol);
        System.out.println("Token generated: " + token);

        return ResponseEntity.ok(new JwtResponse(token, rol, id));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            System.out.println("Authentication successful for user: " + username);
        } catch (BadCredentialsException e) {
            System.out.println("Invalid credentials for user: " + username);
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
