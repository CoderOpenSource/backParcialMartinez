package com.example.examen2.reportes.service;

import com.example.examen2.config.cloudinary.CloudinaryService;
import com.example.examen2.consultas.model.Consulta;
import com.example.examen2.procedimientos.model.Procedimiento;
import com.example.examen2.analisis.model.Analisis;
import com.example.examen2.consultas.repository.ConsultaRepository;
import com.example.examen2.procedimientos.repository.ProcedimientoRepository;
import com.example.examen2.analisis.repository.AnalisisRepository;
import com.example.examen2.reportes.model.Reporte;
import com.example.examen2.reportes.repository.ReporteRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.time.LocalDate;
import java.time.ZoneId;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class ReporteService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ProcedimientoRepository procedimientoRepository;

    @Autowired
    private AnalisisRepository analisisRepository;

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public Reporte generarYSubirReporte(String tipo, String formato, Long pacienteId, Long medicoId, Long especialidadId, Date fechaInicio, Date fechaFin) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String contentType;
            String fileExtension;

            // Generar el título del reporte automáticamente
            String titulo = "Reporte de " + tipo + " - " + LocalDate.now();

            // Generar el archivo según el formato
            if ("pdf".equalsIgnoreCase(formato)) {
                contentType = "application/pdf";
                fileExtension = ".pdf";
                ByteArrayInputStream pdfStream = generarReportePdf(tipo, pacienteId, medicoId, especialidadId, fechaInicio, fechaFin);
                pdfStream.transferTo(out); // Transferir contenido al `ByteArrayOutputStream`
            } else if ("excel".equalsIgnoreCase(formato)) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileExtension = ".xlsx";

                // Generar el archivo Excel
                ByteArrayInputStream excelStream = generarReporteExcel(tipo, pacienteId, medicoId, especialidadId, fechaInicio, fechaFin);

                // Leer y convertir el InputStream en un byte array para subir a Cloudinary
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int nRead;
                while ((nRead = excelStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] excelBytes = buffer.toByteArray();

                // Subir a Cloudinary
                String fileName = titulo.replaceAll("\\s", "_") + "_" + System.currentTimeMillis() + fileExtension;
                Map uploadResult = cloudinaryService.uploadArchivosExcel(excelBytes, contentType, fileName);

                // Obtener la URL del archivo subido
                String archivoUrl = uploadResult.get("url").toString();

                // Crear y guardar el reporte en la base de datos
                Reporte reporte = new Reporte();
                reporte.setTitulo(titulo); // Usar el título generado automáticamente
                reporte.setTipo(formato.toUpperCase());
                reporte.setFechaGeneracion(LocalDate.now());
                reporte.setParametros("PacienteId=" + pacienteId + ", MedicoId=" + medicoId + ", EspecialidadId=" + especialidadId);
                reporte.setArchivoUrl(archivoUrl);

                return reporteRepository.save(reporte);
            } else {
                throw new IllegalArgumentException("Formato no soportado: " + formato);
            }

            // Subir el archivo generado a Cloudinary (para PDFs)
            String fileName = titulo.replaceAll("\\s", "_") + "_" + System.currentTimeMillis() + fileExtension;
            Map uploadResult = cloudinaryService.uploadArchivos(
                    new ByteArrayInputStream(out.toByteArray()),
                    contentType
            );

            // Obtener la URL del archivo subido
            String archivoUrl = uploadResult.get("url").toString();

            // Crear y guardar el reporte en la base de datos
            Reporte reporte = new Reporte();
            reporte.setTitulo(titulo); // Usar el título generado automáticamente
            reporte.setTipo(formato.toUpperCase());
            reporte.setFechaGeneracion(LocalDate.now());
            reporte.setParametros("PacienteId=" + pacienteId + ", MedicoId=" + medicoId + ", EspecialidadId=" + especialidadId);
            reporte.setArchivoUrl(archivoUrl);

            return reporteRepository.save(reporte);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar o subir el reporte: " + e.getMessage(), e);
        }
    }



    private ByteArrayInputStream generarReportePdf(String tipo, Long pacienteId, Long medicoId, Long especialidadId, Date fechaInicio, Date fechaFin) {
        // Filtrar datos según el tipo
        switch (tipo.toLowerCase()) {
            case "consultas":
                List<Consulta> consultas = filtrarConsultas(pacienteId, medicoId, especialidadId, fechaInicio, fechaFin);
                return generarPdfConsultas(consultas);
            case "procedimientos":
                List<Procedimiento> procedimientos = filtrarProcedimientos(pacienteId, medicoId, fechaInicio, fechaFin);
                return generarPdfProcedimientos(procedimientos);
            case "analisis":
                List<Analisis> analisis = filtrarAnalisis(pacienteId, fechaInicio, fechaFin);
                return generarPdfAnalisis(analisis);
            default:
                throw new IllegalArgumentException("Tipo de reporte no soportado: " + tipo);
        }
    }

    private ByteArrayInputStream generarReporteExcel(String tipo, Long pacienteId, Long medicoId, Long especialidadId, Date fechaInicio, Date fechaFin) {
        // Filtrar datos según el tipo
        switch (tipo.toLowerCase()) {
            case "consultas":
                List<Consulta> consultas = filtrarConsultas(pacienteId, medicoId, especialidadId, fechaInicio, fechaFin);
                return generarExcelConsultas(consultas);
            case "procedimientos":
                List<Procedimiento> procedimientos = filtrarProcedimientos(pacienteId, medicoId, fechaInicio, fechaFin);
                return generarExcelProcedimientos(procedimientos);
            case "analisis":
                List<Analisis> analisis = filtrarAnalisis(pacienteId, fechaInicio, fechaFin);
                return generarExcelAnalisis(analisis);
            default:
                throw new IllegalArgumentException("Tipo de reporte no soportado: " + tipo);
        }
    }

    private List<Consulta> filtrarConsultas(Long pacienteId, Long medicoId, Long especialidadId, Date fechaInicio, Date fechaFin) {
        LocalDate inicio = fechaInicio != null ? fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate fin = fechaFin != null ? fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        return consultaRepository.findAll().stream()
                .filter(consulta -> pacienteId == null || consulta.getPaciente().getId().equals(pacienteId))
                .filter(consulta -> medicoId == null || consulta.getMedico().getId().equals(medicoId))
                .filter(consulta -> especialidadId == null || consulta.getCita().getEspecialidad().getId().equals(especialidadId))
                .filter(consulta -> inicio == null || !consulta.getFecha().isBefore(inicio))
                .filter(consulta -> fin == null || !consulta.getFecha().isAfter(fin))
                .collect(Collectors.toList());
    }



    private List<Procedimiento> filtrarProcedimientos(Long pacienteId, Long medicoId, Date fechaInicio, Date fechaFin) {
        LocalDate inicio = fechaInicio != null ? fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate fin = fechaFin != null ? fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        return procedimientoRepository.findAll().stream()
                .filter(procedimiento -> pacienteId == null || procedimiento.getPaciente().getId().equals(pacienteId))
                .filter(procedimiento -> medicoId == null || procedimiento.getMedico().getId().equals(medicoId))
                .filter(procedimiento -> inicio == null || !procedimiento.getFecha().isBefore(inicio))
                .filter(procedimiento -> fin == null || !procedimiento.getFecha().isAfter(fin))
                .collect(Collectors.toList());
    }



    private List<Analisis> filtrarAnalisis(Long pacienteId, Date fechaInicio, Date fechaFin) {
        LocalDate inicio = fechaInicio != null ? fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate fin = fechaFin != null ? fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        return analisisRepository.findAll().stream()
                .filter(analisis -> pacienteId == null || analisis.getPaciente().getId().equals(pacienteId))
                .filter(analisis -> inicio == null || !analisis.getFechaRealizacion().isBefore(inicio))
                .filter(analisis -> fin == null || !analisis.getFechaRealizacion().isAfter(fin))
                .collect(Collectors.toList());
    }


    private ByteArrayInputStream generarPdfConsultas(List<Consulta> consultas) {
        // Generar PDF para consultas
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

        // Agregar título al reporte
        document.add(new Paragraph("Reporte de Consultas").setBold().setFontSize(16));

        // Crear tabla con columnas adicionales
        Table table = new Table(8);
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha")));
        table.addHeaderCell(new Cell().add(new Paragraph("Paciente")));
        table.addHeaderCell(new Cell().add(new Paragraph("Médico")));
        table.addHeaderCell(new Cell().add(new Paragraph("Diagnóstico")));
        table.addHeaderCell(new Cell().add(new Paragraph("Síntomas")));
        table.addHeaderCell(new Cell().add(new Paragraph("Tratamiento")));
        table.addHeaderCell(new Cell().add(new Paragraph("Notas")));

        for (Consulta consulta : consultas) {
            table.addCell(new Cell().add(new Paragraph(consulta.getId().toString())));
            table.addCell(new Cell().add(new Paragraph(consulta.getFecha().toString())));
            table.addCell(new Cell().add(new Paragraph(consulta.getPaciente().getUsuario().getNombre() + " " +
                    consulta.getPaciente().getUsuario().getApellido())));
            table.addCell(new Cell().add(new Paragraph(consulta.getMedico().getUsuario().getNombre() + " " +
                    consulta.getMedico().getUsuario().getApellido())));
            table.addCell(new Cell().add(new Paragraph(consulta.getDiagnostico())));
            table.addCell(new Cell().add(new Paragraph(String.join(", ", consulta.getSintomas()))));
            table.addCell(new Cell().add(new Paragraph(consulta.getTratamiento())));
            table.addCell(new Cell().add(new Paragraph(consulta.getNotas() != null ? consulta.getNotas() : "Sin notas")));
        }

        document.add(table);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }


    private ByteArrayInputStream generarPdfProcedimientos(List<Procedimiento> procedimientos) {
        // Generar PDF para procedimientos
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

        // Agregar título
        document.add(new Paragraph("Reporte de Procedimientos").setBold().setFontSize(16));

        // Crear tabla con columnas adicionales
        Table table = new Table(7);
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha")));
        table.addHeaderCell(new Cell().add(new Paragraph("Paciente")));
        table.addHeaderCell(new Cell().add(new Paragraph("Médico")));
        table.addHeaderCell(new Cell().add(new Paragraph("Especialidad")));
        table.addHeaderCell(new Cell().add(new Paragraph("Descripción")));
        table.addHeaderCell(new Cell().add(new Paragraph("Observaciones")));

        for (Procedimiento procedimiento : procedimientos) {
            table.addCell(new Cell().add(new Paragraph(procedimiento.getId().toString())));
            table.addCell(new Cell().add(new Paragraph(procedimiento.getFecha().toString())));
            table.addCell(new Cell().add(new Paragraph(procedimiento.getPaciente().getUsuario().getNombre() +
                    " " + procedimiento.getPaciente().getUsuario().getApellido())));
            table.addCell(new Cell().add(new Paragraph(procedimiento.getMedico().getUsuario().getNombre() +
                    " " + procedimiento.getMedico().getUsuario().getApellido())));
            table.addCell(new Cell().add(new Paragraph(procedimiento.getConsulta().getCita().getEspecialidad().getNombre())));
            table.addCell(new Cell().add(new Paragraph(procedimiento.getDescripcion())));
            table.addCell(new Cell().add(new Paragraph(procedimiento.getObservaciones() != null
                    ? procedimiento.getObservaciones() : "Sin Observaciones")));
        }

        document.add(table);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }


    private ByteArrayInputStream generarPdfAnalisis(List<Analisis> analisis) {
        // Generar PDF para análisis
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

        // Agregar título
        document.add(new Paragraph("Reporte de Análisis").setBold().setFontSize(16));

        // Crear tabla con columnas adicionales
        Table table = new Table(8);
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha")));
        table.addHeaderCell(new Cell().add(new Paragraph("Paciente")));
        table.addHeaderCell(new Cell().add(new Paragraph("Médico")));
        table.addHeaderCell(new Cell().add(new Paragraph("Tipo de Análisis")));
        table.addHeaderCell(new Cell().add(new Paragraph("Resultado")));
        table.addHeaderCell(new Cell().add(new Paragraph("Archivo URL")));

        for (Analisis analisisItem : analisis) {
            table.addCell(new Cell().add(new Paragraph(analisisItem.getId().toString())));
            table.addCell(new Cell().add(new Paragraph(analisisItem.getFechaRealizacion().toString())));
            table.addCell(new Cell().add(new Paragraph(analisisItem.getPaciente().getUsuario().getNombre() +
                    " " + analisisItem.getPaciente().getUsuario().getApellido())));
            table.addCell(new Cell().add(new Paragraph(analisisItem.getMedico().getUsuario().getNombre() +
                    " " + analisisItem.getMedico().getUsuario().getApellido())));
            table.addCell(new Cell().add(new Paragraph(analisisItem.getTipoAnalisis())));
            table.addCell(new Cell().add(new Paragraph(analisisItem.getResultado())));
            table.addCell(new Cell().add(new Paragraph(analisisItem.getArchivoUrl() != null
                    ? analisisItem.getArchivoUrl() : "Sin Archivo")));
        }

        document.add(table);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }


    private ByteArrayInputStream generarExcelConsultas(List<Consulta> consultas) {
        // Generar Excel para consultas
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Sheet sheet = workbook.createSheet("Consultas");

        // Crear encabezado con columnas adicionales
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Fecha");
        header.createCell(2).setCellValue("Paciente");
        header.createCell(3).setCellValue("Médico");
        header.createCell(4).setCellValue("Diagnóstico");
        header.createCell(5).setCellValue("Síntomas");
        header.createCell(6).setCellValue("Tratamiento");
        header.createCell(7).setCellValue("Notas");
        header.createCell(8).setCellValue("Derivó Procedimiento");

        int rowIdx = 1;
        for (Consulta consulta : consultas) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(consulta.getId());
            row.createCell(1).setCellValue(consulta.getFecha().toString());
            row.createCell(2).setCellValue(consulta.getPaciente().getUsuario().getNombre() + " " +
                    consulta.getPaciente().getUsuario().getApellido());
            row.createCell(3).setCellValue(consulta.getMedico().getUsuario().getNombre() + " " +
                    consulta.getMedico().getUsuario().getApellido());
            row.createCell(4).setCellValue(consulta.getDiagnostico());
            row.createCell(5).setCellValue(String.join(", ", consulta.getSintomas()));
            row.createCell(6).setCellValue(consulta.getTratamiento());
            row.createCell(7).setCellValue(consulta.getNotas() != null ? consulta.getNotas() : "Sin notas");
            row.createCell(8).setCellValue(consulta.getDerivoProcedimiento() ? "Sí" : "No");
        }

        try {
            workbook.write(out);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel para consultas", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    private ByteArrayInputStream generarExcelProcedimientos(List<Procedimiento> procedimientos) {
        // Crear el libro de Excel
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Sheet sheet = workbook.createSheet("Procedimientos");

        // Crear encabezados
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Fecha");
        header.createCell(2).setCellValue("Paciente");
        header.createCell(3).setCellValue("Médico");
        header.createCell(4).setCellValue("Especialidad del Médico");
        header.createCell(5).setCellValue("Consulta Relacionada");
        header.createCell(6).setCellValue("Descripción");
        header.createCell(7).setCellValue("Observaciones");
        header.createCell(8).setCellValue("Archivo URL");

        // Agregar datos de los procedimientos
        int rowIdx = 1;
        for (Procedimiento procedimiento : procedimientos) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(procedimiento.getId()); // ID del procedimiento
            row.createCell(1).setCellValue(procedimiento.getFecha().toString()); // Fecha del procedimiento
            row.createCell(2).setCellValue(procedimiento.getPaciente().getUsuario().getNombre()
                    + " " + procedimiento.getPaciente().getUsuario().getApellido()); // Nombre del paciente
            row.createCell(3).setCellValue(procedimiento.getMedico().getUsuario().getNombre()
                    + " " + procedimiento.getMedico().getUsuario().getApellido()); // Nombre del médico
            row.createCell(4).setCellValue(procedimiento.getConsulta().getCita().getEspecialidad().getNombre()); // Especialidad del médico
            row.createCell(5).setCellValue(procedimiento.getConsulta().getId() != null
                    ? "Consulta #" + procedimiento.getConsulta().getId() : "Sin Consulta"); // ID de la consulta relacionada
            row.createCell(6).setCellValue(procedimiento.getDescripcion()); // Descripción del procedimiento
            row.createCell(7).setCellValue(procedimiento.getObservaciones() != null
                    ? procedimiento.getObservaciones() : "Sin Observaciones"); // Observaciones
            row.createCell(8).setCellValue(procedimiento.getArchivoUrl() != null
                    ? procedimiento.getArchivoUrl() : "Sin Archivo"); // URL del archivo relacionado
        }

        try {
            workbook.write(out);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel para procedimientos", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


    private ByteArrayInputStream generarExcelAnalisis(List<Analisis> analisis) {
        // Crear el libro de Excel
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Sheet sheet = workbook.createSheet("Análisis");

        // Crear encabezados
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Fecha de Realización");
        header.createCell(2).setCellValue("Paciente");
        header.createCell(3).setCellValue("Médico");
        header.createCell(5).setCellValue("Tipo de Análisis");
        header.createCell(6).setCellValue("Resultado");
        header.createCell(7).setCellValue("Archivo URL");

        // Agregar datos de los análisis
        int rowIdx = 1;
        for (Analisis analisisItem : analisis) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(analisisItem.getId()); // ID del análisis
            row.createCell(1).setCellValue(analisisItem.getFechaRealizacion().toString()); // Fecha de realización
            row.createCell(2).setCellValue(analisisItem.getPaciente().getUsuario().getNombre()
                    + " " + analisisItem.getPaciente().getUsuario().getApellido()); // Nombre del paciente
            row.createCell(3).setCellValue(analisisItem.getMedico().getUsuario().getNombre()
                    + " " + analisisItem.getMedico().getUsuario().getApellido()); // Nombre del médico
            row.createCell(5).setCellValue(analisisItem.getTipoAnalisis()); // Tipo de análisis
            row.createCell(6).setCellValue(analisisItem.getResultado()); // Resultado
            row.createCell(7).setCellValue(analisisItem.getArchivoUrl()); // URL del archivo asociado
        }

        try {
            workbook.write(out);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel para análisis", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }




    // Método para obtener todos los reportes
    public List<Reporte> obtenerTodosLosReportes() {
        return reporteRepository.findAll();
    }

    // Método para eliminar un reporte por ID
    public void eliminarReportePorId(Long id) {
        reporteRepository.deleteById(id);
    }
}
