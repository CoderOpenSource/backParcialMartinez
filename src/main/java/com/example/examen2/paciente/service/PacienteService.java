package com.example.examen2.paciente.service;

import com.example.examen2.historialclinico.model.HistorialClinico;
import com.example.examen2.historialclinico.repository.HistorialClinicoRepository;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.paciente.repository.PacienteRepository;
import com.example.examen2.usuarios.model.Usuario;
import com.example.examen2.usuarios.model.Rol;
import com.example.examen2.usuarios.repository.UsuarioRepository;
import com.example.examen2.usuarios.repository.RolRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistorialClinicoRepository historialClinicoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private EmailService emailService;
    // Definir el logger
    private static final Logger logger = LoggerFactory.getLogger(PacienteService.class);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAll();
    }

    public void cargarExcel(MultipartFile file) throws IOException {
        Rol rolPaciente = rolRepository.findById(3L).orElseThrow(() -> new IllegalStateException("Rol de paciente no encontrado"));
        PdfGenerator pdfGenerator = new PdfGenerator();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next(); // Saltar la primera fila si es encabezado
            }

            while (rows.hasNext()) {
                Row row = rows.next();

                String nombre = getStringCellValue(row, 0);
                String apellido = getStringCellValue(row, 1);
                String email = getStringCellValue(row, 2);
                String carnet = getStringCellValue(row, 3);
                LocalDate fechaNacimiento = parseDate(getStringCellValue(row, 4));
                String sexo = getStringCellValue(row, 5);
                int edad = (int) getNumericCellValue(row, 6);
                LocalDate fechaInicioSeguro = parseDate(getStringCellValue(row, 7));
                LocalDate fechaFinSeguro = parseDate(getStringCellValue(row, 8));

                Optional<Paciente> pacienteExistente = pacienteRepository.findByCarnet(carnet);

                if (pacienteExistente.isPresent()) {
                    Paciente existente = pacienteExistente.get();
                    existente.setFechaNacimiento(fechaNacimiento);
                    existente.setSexo(sexo);
                    existente.setEdad(edad);
                    existente.setFechaInicioSeguro(fechaInicioSeguro);
                    existente.setFechaFinSeguro(fechaFinSeguro);

                    Usuario usuario = existente.getUsuario();
                    usuario.setNombre(nombre);
                    usuario.setApellido(apellido);
                    usuario.setEmail(email);
                    usuario.setContrasena(passwordEncoder.encode(carnet));
                    usuario.setRol(rolPaciente);
                    usuarioRepository.save(usuario);

                    existente.setUsuario(usuario);
                    pacienteRepository.save(existente);

                    // Actualizar o registrar el historial clínico si no existe
                    historialClinicoRepository.findByPacienteId(existente.getId())
                            .orElseGet(() -> {
                                HistorialClinico nuevoHistorial = new HistorialClinico();
                                nuevoHistorial.setPaciente(existente);
                                nuevoHistorial.setFechaCreacion(LocalDate.now());
                                nuevoHistorial.setFechaUltimaActualizacion(LocalDate.now());
                                nuevoHistorial.setArchivoUrl(null); // Inicialmente vacío
                                return historialClinicoRepository.save(nuevoHistorial);
                            });

                } else {
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setNombre(nombre);
                    nuevoUsuario.setApellido(apellido);
                    nuevoUsuario.setEmail(email);
                    nuevoUsuario.setContrasena(passwordEncoder.encode(carnet));
                    nuevoUsuario.setRol(rolPaciente);
                    usuarioRepository.save(nuevoUsuario);

                    Paciente nuevoPaciente = new Paciente();
                    nuevoPaciente.setCarnet(carnet);
                    nuevoPaciente.setFechaNacimiento(fechaNacimiento);
                    nuevoPaciente.setSexo(sexo);
                    nuevoPaciente.setEdad(edad);
                    nuevoPaciente.setFechaInicioSeguro(fechaInicioSeguro);
                    nuevoPaciente.setFechaFinSeguro(fechaFinSeguro);
                    nuevoPaciente.setUsuario(nuevoUsuario);

                    Paciente pacienteGuardado = pacienteRepository.save(nuevoPaciente);

                    // Registrar el historial clínico
                    HistorialClinico nuevoHistorial = new HistorialClinico();
                    nuevoHistorial.setPaciente(pacienteGuardado);
                    nuevoHistorial.setFechaCreacion(LocalDate.now());
                    nuevoHistorial.setFechaUltimaActualizacion(LocalDate.now());
                    nuevoHistorial.setArchivoUrl(null); // Inicialmente vacío
                    historialClinicoRepository.save(nuevoHistorial);

                }

                // Verificar si el seguro ha expirado y enviar correo con PDF si es necesario
                if (fechaFinSeguro != null) {
                    logger.info("Fecha de fin de seguro de {} {}: {}", nombre, apellido, fechaFinSeguro);
                    logger.info("Fecha actual: {}", LocalDate.now());

                    if (fechaFinSeguro.isBefore(LocalDate.now())) {
                        logger.info("El seguro ha expirado para {} {}. Enviando correo...", nombre, apellido);

                        File pdfFile = pdfGenerator.generateExpiredInsurancePdf(nombre, apellido, fechaFinSeguro);

                        String asunto = "Aviso de Seguro Expirado";
                        String cuerpo = "Estimado(a) " + nombre + ",\n\nLe informamos que su seguro ha expirado. Consulte el archivo adjunto para más detalles.";
                        emailService.enviarCorreoConPDF(email, asunto, cuerpo, pdfFile);

                        pdfFile.delete(); // Eliminar el archivo temporal después de enviarlo
                    } else {
                        logger.info("El seguro está vigente para {} {}. No se enviará correo.", nombre, apellido);
                    }
                } else {
                    logger.warn("Fecha de fin de seguro es null para {} {}. No se puede determinar el estado del seguro.", nombre, apellido);
                }
            }
        }
    }

    private String getStringCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private double getNumericCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return cell != null && cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() : 0;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Error al parsear la fecha con el formato 'dd/MM/yyyy': " + dateStr);
            // Intentar otro formato, como 'yyyy-MM-dd', si es necesario
            try {
                DateTimeFormatter alternateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(dateStr, alternateFormatter);
            } catch (DateTimeParseException ex) {
                System.err.println("Error al parsear la fecha con el formato alternativo 'yyyy-MM-dd': " + dateStr + ". Usando null.");
                return null;
            }
        }
    }
    public Optional<Paciente> findById(Long id) {
        return pacienteRepository.findById(id);
    }

}
