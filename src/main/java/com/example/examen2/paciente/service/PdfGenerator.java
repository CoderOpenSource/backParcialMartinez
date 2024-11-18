package com.example.examen2.paciente.service;

import com.example.examen2.analisis.model.Analisis;
import com.example.examen2.consultas.model.Consulta;
import com.example.examen2.paciente.model.Paciente;
import com.example.examen2.procedimientos.model.Procedimiento;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
@Service
public class PdfGenerator {

    public File generateExpiredInsurancePdf(String nombre, String apellido, LocalDate fechaFinSeguro) throws IOException {
        String filePath = System.getProperty("java.io.tmpdir") + "/SeguroExpirado_" + nombre + "_" + apellido + ".pdf";
        File pdfFile = new File(filePath);

        // Crear el PdfWriter y el Document
        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Añadir contenido al documento
        document.add(new Paragraph("Aviso de Seguro Expirado")
                .setFontSize(18)
                .setFontColor(ColorConstants.RED)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold());

        document.add(new Paragraph("\nEstimado(a) " + nombre + " " + apellido + ","));
        document.add(new Paragraph("Le informamos que su seguro ha expirado en la fecha: " + fechaFinSeguro.toString() + "."));
        document.add(new Paragraph("Por favor, póngase en contacto con nosotros para renovar su seguro."));
        document.add(new Paragraph("\nGracias por su atención."));

        // Cerrar el documento
        document.close();

        return pdfFile;
    }

    public ByteArrayInputStream generateHistorialClinicoPdf(Paciente paciente, List<Consulta> consultas, List<Procedimiento> procedimientos, List<Analisis> analisis) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Crear el PdfWriter y el Document
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);

        Document document = new Document(pdfDoc);
        document.setMargins(20, 20, 20, 20);

        // Cabecera
        agregarCabecera(document);

        // Información básica del paciente
        agregarInformacionPaciente(document, paciente);

        // Consultas
        agregarConsultas(document, consultas);

        // Procedimientos
        agregarProcedimientos(document, procedimientos);

        // Análisis
        agregarAnalisis(document, analisis);

        // Pie de página
        agregarPieDePagina(document);

        // Cerrar el documento
        document.close();

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private void agregarAnalisis(Document document, List<Analisis> analisis) {
        document.add(new Paragraph("Análisis")
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10));

        if (analisis != null && !analisis.isEmpty()) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 4})).useAllAvailableWidth();
            table.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Tipo de Análisis").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Resultado").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Archivo").setBold()));

            for (Analisis analisisItem : analisis) {
                table.addCell(new Cell().add(new Paragraph(analisisItem.getFechaRealizacion().toString())));
                table.addCell(new Cell().add(new Paragraph(analisisItem.getTipoAnalisis())));
                table.addCell(new Cell().add(new Paragraph(analisisItem.getResultado())));
                if (analisisItem.getArchivoUrl() != null) {
                    table.addCell(new Cell().add(new Paragraph(analisisItem.getArchivoUrl()))
                            .setFontColor(ColorConstants.BLUE)); // Enlace en azul
                } else {
                    table.addCell(new Cell().add(new Paragraph("Sin archivo")));
                }
            }

            document.add(table);
        } else {
            document.add(new Paragraph("No hay análisis registrados.")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT));
        }

        document.add(new Paragraph("\n")); // Espaciado
    }


    private void agregarCabecera(Document document) {
        // Crear la tabla para la cabecera
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();
        headerTable.setBackgroundColor(ColorConstants.LIGHT_GRAY);

        // Título de la clínica
        Cell titleCell = new Cell().add(new Paragraph("SSVS")
                        .setFontSize(20) // Tamaño de fuente grande
                        .setBold() // Negrita
                        .setTextAlignment(TextAlignment.CENTER) // Centrado
                        .setFontColor(ColorConstants.BLACK)) // Color negro
                .setBorder(Border.NO_BORDER) // Sin borde
                .setPadding(10); // Espaciado interno

        // Añadir el título a la tabla
        headerTable.addCell(titleCell);

        // Agregar la tabla al documento
        document.add(headerTable);
        document.add(new Paragraph("\n")); // Espaciado extra después de la cabecera
    }


    private void agregarInformacionPaciente(Document document, Paciente paciente) {
        document.add(new Paragraph("Información del Paciente")
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        table.addCell(new Cell().add(new Paragraph("Nombre Completo:")).setBold());
        table.addCell(new Cell().add(new Paragraph(paciente.getUsuario().getNombre() + " " + paciente.getUsuario().getApellido())));
        table.addCell(new Cell().add(new Paragraph("Carnet:")).setBold());
        table.addCell(new Cell().add(new Paragraph(paciente.getCarnet())));
        table.addCell(new Cell().add(new Paragraph("Fecha de Nacimiento:")).setBold());
        table.addCell(new Cell().add(new Paragraph(paciente.getFechaNacimiento().toString())));

        document.add(table);
        document.add(new Paragraph("\n")); // Espaciado
    }

    private void agregarConsultas(Document document, List<Consulta> consultas) {
        document.add(new Paragraph("Consultas")
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10));

        if (consultas != null && !consultas.isEmpty()) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 3})).useAllAvailableWidth();
            table.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Diagnóstico").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Tratamiento").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Notas").setBold()));

            for (Consulta consulta : consultas) {
                table.addCell(new Cell().add(new Paragraph(consulta.getFecha().toString())));
                table.addCell(new Cell().add(new Paragraph(consulta.getDiagnostico())));
                table.addCell(new Cell().add(new Paragraph(consulta.getTratamiento())));
                table.addCell(new Cell().add(new Paragraph(consulta.getNotas())));
            }

            document.add(table);
        } else {
            document.add(new Paragraph("No hay consultas registradas.")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT));
        }

        document.add(new Paragraph("\n")); // Espaciado
    }

    private void agregarProcedimientos(Document document, List<Procedimiento> procedimientos) {
        document.add(new Paragraph("Procedimientos")
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10));

        if (procedimientos != null && !procedimientos.isEmpty()) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3})).useAllAvailableWidth();
            table.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Descripción").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Observaciones").setBold()));

            for (Procedimiento procedimiento : procedimientos) {
                table.addCell(new Cell().add(new Paragraph(procedimiento.getFecha().toString())));
                table.addCell(new Cell().add(new Paragraph(procedimiento.getDescripcion())));
                table.addCell(new Cell().add(new Paragraph(procedimiento.getObservaciones())));
            }

            document.add(table);
        } else {
            document.add(new Paragraph("No hay procedimientos registrados.")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT));
        }

        document.add(new Paragraph("\n")); // Espaciado
    }

    private void agregarPieDePagina(Document document) {
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Documento generado automáticamente por el sistema")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
    }

}
