package com.example.examen2.notification.controller;


import com.example.examen2.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint para enviar una notificación push
     *
     * @param token Token del dispositivo que recibirá la notificación
     * @param title Título de la notificación
     * @param body Cuerpo de la notificación
     * @return Respuesta de éxito
     */
    @PostMapping("/send")
    public String sendNotification(@RequestParam String token,
                                   @RequestParam String title,
                                   @RequestParam String body) {
        notificationService.sendNotification(token, title, body);
        return "Notificación enviada con éxito!";
    }
}
