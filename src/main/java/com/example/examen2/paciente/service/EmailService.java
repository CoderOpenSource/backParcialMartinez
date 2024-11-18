package com.example.examen2.paciente.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Método para enviar correo con PDF adjunto
    public void enviarCorreoConPDF(String destinatario, String asunto, String cuerpo, File archivoPDF) {
        MimeMessage mensaje = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo);

            // Adjuntar el archivo PDF
            FileSystemResource file = new FileSystemResource(archivoPDF);
            helper.addAttachment(archivoPDF.getName(), file);

            // Enviar el correo
            mailSender.send(mensaje);
            System.out.println("Correo enviado con éxito a " + destinatario);

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
    // Método para enviar correo sin archivo adjunto
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        MimeMessage mensaje = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo);

            // Enviar el correo
            mailSender.send(mensaje);
            System.out.println("Correo enviado con éxito a " + destinatario);

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }

}
