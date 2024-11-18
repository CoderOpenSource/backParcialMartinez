package com.example.examen2.medicos.service;

import com.example.examen2.especialidades.model.Especialidad;
import com.example.examen2.especialidades.repository.EspecialidadRepository;
import com.example.examen2.medicos.model.Medico;
import com.example.examen2.medicos.model.MedicoDTO;
import com.example.examen2.medicos.repository.MedicoRepository;
import com.example.examen2.usuarios.model.Rol;
import com.example.examen2.usuarios.model.Usuario;
import com.example.examen2.usuarios.repository.RolRepository;
import com.example.examen2.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



        private static final Logger logger = LoggerFactory.getLogger(MedicoService.class);

        public Medico createMedico(MedicoDTO medicoDTO) {
            logger.info("Creando médico con CI: {}", medicoDTO.getCi());

            Usuario usuario = new Usuario();
            usuario.setNombre(medicoDTO.getNombre());
            usuario.setApellido(medicoDTO.getApellido());
            usuario.setEmail(medicoDTO.getEmail());
            usuario.setContrasena(passwordEncoder.encode(medicoDTO.getCi()));

            logger.info("Contraseña encriptada para el usuario: {}", usuario.getContrasena());

            Rol rolMedico = rolRepository.findByNombre("MEDICO");
            if (rolMedico == null) {
                throw new RuntimeException("El rol de MEDICO no existe.");
            }
            usuario.setRol(rolMedico);
            usuarioRepository.save(usuario);

            Set<Especialidad> especialidades = new HashSet<>(especialidadRepository.findAllById(medicoDTO.getEspecialidadesIds()));
            logger.info("Especialidades asignadas: {}", especialidades);

            Medico medico = new Medico();
            medico.setCi(medicoDTO.getCi());
            medico.setUsuario(usuario);
            medico.setEspecialidades(especialidades);
            medico.setTelefonoConsultorio(medicoDTO.getTelefonoConsultorio());
            medico.setNumeroLicencia(medicoDTO.getNumeroLicencia());
            medico.setDisponibilidad(medicoDTO.getDisponibilidad());

            medicoRepository.save(medico);
            logger.info("Médico creado con éxito: {}", medico.getId());

            return medico;
        }



    public List<MedicoDTO> getAllMedicosDTO() {
        List<Medico> medicos = medicoRepository.findAll();
        return medicos.stream().map(this::convertToMedicoDTO).collect(Collectors.toList());
    }

    public Optional<MedicoDTO> getMedicoDTOById(Long id) {
        return medicoRepository.findById(id).map(this::convertToMedicoDTO);
    }

    public MedicoDTO convertToMedicoDTO(Medico medico) {
        MedicoDTO medicoDTO = new MedicoDTO();
        medicoDTO.setId(medico.getId()); // Set ID
        medicoDTO.setCi(medico.getCi());
        medicoDTO.setNombre(medico.getUsuario().getNombre());
        medicoDTO.setApellido(medico.getUsuario().getApellido());
        medicoDTO.setEmail(medico.getUsuario().getEmail());
        medicoDTO.setTelefonoConsultorio(medico.getTelefonoConsultorio());
        medicoDTO.setNumeroLicencia(medico.getNumeroLicencia());
        medicoDTO.setDisponibilidad(medico.getDisponibilidad());
        medicoDTO.setUsuarioId(medico.getUsuario().getId()); // Set Usuario ID

        List<Long> especialidadesIds = medico.getEspecialidades().stream()
                .map(Especialidad::getId)
                .collect(Collectors.toList());
        medicoDTO.setEspecialidadesIds(especialidadesIds);

        return medicoDTO;
    }




    public Medico updateMedico(Long id, MedicoDTO medicoDTO) {
        return medicoRepository.findById(id).map(medico -> {
            // Actualizar los campos básicos del médico
            medico.setCi(medicoDTO.getCi());
            medico.setTelefonoConsultorio(medicoDTO.getTelefonoConsultorio());
            medico.setNumeroLicencia(medicoDTO.getNumeroLicencia());
            medico.setDisponibilidad(medicoDTO.getDisponibilidad());

            // Actualizar especialidades
            Set<Especialidad> especialidades = new HashSet<>(especialidadRepository.findAllById(medicoDTO.getEspecialidadesIds()));
            medico.setEspecialidades(especialidades);

            // Actualizar el usuario relacionado
            Usuario usuario = medico.getUsuario();
            usuario.setNombre(medicoDTO.getNombre());
            usuario.setApellido(medicoDTO.getApellido());
            usuario.setEmail(medicoDTO.getEmail());

            // Si el CI cambia, actualizar la contraseña
            if (!usuario.getContrasena().equals(passwordEncoder.encode(medicoDTO.getCi()))) {
                usuario.setContrasena(passwordEncoder.encode(medicoDTO.getCi()));
            }

            // Guardar los cambios en el usuario
            usuarioRepository.save(usuario);

            // Guardar los cambios en el médico
            return medicoRepository.save(medico);
        }).orElseThrow(() -> new RuntimeException("Médico no encontrado con ID: " + id));
    }


    public void deleteMedico(Long id) {
        medicoRepository.deleteById(id);
    }
    public Optional<Medico> findById(Long id) {
        return medicoRepository.findById(id);
    }
}
