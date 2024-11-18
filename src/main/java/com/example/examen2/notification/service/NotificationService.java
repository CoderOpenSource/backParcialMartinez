package com.example.examen2.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(String token, String title, String body) {
        try {
            // Crear un mensaje de notificación
            Message message = Message.builder()
                    .setToken(token) // Token del dispositivo
                    .setNotification(Notification.builder()
                            .setTitle(title) // Título de la notificación
                            .setBody(body)   // Cuerpo de la notificación
                            .build())
                    .build();

            // Enviar la notificación
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notificación enviada con éxito: " + response);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al enviar la notificación: " + e.getMessage());
        }
    }
}
