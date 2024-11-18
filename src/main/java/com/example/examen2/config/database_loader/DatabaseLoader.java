package com.example.examen2.config.database_loader;

import com.example.examen2.especialidades.model.Especialidad;
import com.example.examen2.especialidades.repository.EspecialidadRepository;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.medicos.repository.MedicoRepository;
import com.example.examen2.usuarios.model.Rol;
import com.example.examen2.usuarios.model.Usuario;
import com.example.examen2.usuarios.repository.RolRepository;
import com.example.examen2.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import com.example.examen2.horarios.model.Horario;
import com.example.examen2.horarios.repository.HorarioRepository;
import com.github.javafaker.Faker;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(
            RolRepository rolRepository,
            EspecialidadRepository especialidadRepository,
            MedicoRepository medicoRepository,
            UsuarioRepository usuarioRepository,
            HorarioRepository horarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            Faker faker = new Faker(); // Instancia de Java Faker

            // Crear roles si no existen
            if (rolRepository.count() == 0) {
                rolRepository.save(new Rol("ADMIN"));
                rolRepository.save(new Rol("MEDICO"));
                rolRepository.save(new Rol("PACIENTE"));
            }

            // Obtener los roles
            Rol rolAdmin = rolRepository.findByNombre("ADMIN");
            Rol rolMedico = rolRepository.findByNombre("MEDICO");

            // Crear un usuario administrador si no existe
            if (usuarioRepository.findByEmail("admin@example.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setApellido("Principal");
                admin.setEmail("admin@example.com");
                admin.setContrasena(passwordEncoder.encode("admin123")); // Contraseña hasheada
                admin.setRol(rolAdmin);
                usuarioRepository.save(admin);
            }

            // Cargar especialidades si no existen
            if (especialidadRepository.count() == 0) {
                especialidadRepository.save(new Especialidad("Cardiología", "Especialidad que trata enfermedades del corazón"));
                especialidadRepository.save(new Especialidad("Dermatología", "Especialidad que se ocupa de las enfermedades de la piel"));
                especialidadRepository.save(new Especialidad("Neurología", "Especialidad que trata las enfermedades del sistema nervioso"));
                especialidadRepository.save(new Especialidad("Pediatría", "Especialidad que se centra en la salud de los niños y adolescentes"));
                especialidadRepository.save(new Especialidad("Oncología", "Especialidad que se ocupa del estudio y tratamiento del cáncer"));
                especialidadRepository.save(new Especialidad("Gastroenterología", "Especialidad que trata enfermedades del sistema digestivo"));
                especialidadRepository.save(new Especialidad("Neumología", "Especialidad que estudia y trata enfermedades del sistema respiratorio"));
                especialidadRepository.save(new Especialidad("Endocrinología", "Especialidad que trata enfermedades hormonales"));
                especialidadRepository.save(new Especialidad("Reumatología", "Especialidad que se ocupa de las enfermedades articulares y reumáticas"));
                especialidadRepository.save(new Especialidad("Ginecología", "Especialidad que se centra en la salud del sistema reproductor femenino"));
            }

            // Solo crea médicos si no existen médicos en la base de datos
            if (medicoRepository.count() == 0) {
                // Obtener todas las especialidades en una lista
                List<Especialidad> especialidades = especialidadRepository.findAll();
                Random random = new Random();

                // Crear y guardar 10 médicos con datos generados
                for (int i = 0; i < 10; i++) {
                    String ci = faker.number().digits(8); // Generar CI aleatorio
                    String nombre = faker.name().firstName(); // Nombre aleatorio
                    String apellido = faker.name().lastName(); // Apellido aleatorio
                    String email = faker.internet().emailAddress(); // Email aleatorio

                    // Crear el usuario asociado al médico
                    Usuario usuario = new Usuario();
                    usuario.setNombre(nombre);
                    usuario.setApellido(apellido);
                    usuario.setEmail(email);
                    usuario.setContrasena(passwordEncoder.encode(ci)); // Contraseña
                    usuario.setRol(rolMedico);
                    usuarioRepository.save(usuario);

                    // Asignar aleatoriamente entre 2 y 3 especialidades al médico
                    Set<Especialidad> especialidadesMedico = new HashSet<>();
                    int numeroEspecialidades = 2 + random.nextInt(2); // Genera 2 o 3 especialidades
                    while (especialidadesMedico.size() < numeroEspecialidades) {
                        especialidadesMedico.add(especialidades.get(random.nextInt(especialidades.size())));
                    }

                    // Crear el médico y asignar especialidades y otros datos
                    Medico medico = new Medico();
                    medico.setCi(ci);
                    medico.setUsuario(usuario);
                    medico.setEspecialidades(especialidadesMedico);
                    medico.setTelefonoConsultorio(faker.phoneNumber().phoneNumber()); // Teléfono aleatorio
                    medico.setNumeroLicencia("LIC-" + faker.number().digits(5)); // Licencia aleatoria
                    medico.setDisponibilidad(true);
                    medicoRepository.save(medico);
                }
            }
        };
    }
}
