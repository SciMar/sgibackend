package com.sgi.backend.service;

import com.sgi.backend.model.Usuario;
import com.sgi.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Enviar correo masivo a todos los usuarios del sistema
     */
    public int enviarCorreoMasivo(String asunto, String mensaje) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<String> emails = usuarios.stream()
                .filter(u -> u.getActivo())
                .map(Usuario::getEmail)
                .filter(email -> email != null && !email.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (emails.isEmpty()) {
            throw new RuntimeException("No se encontraron emails de usuarios");
        }

        // Crear mensajes
        SimpleMailMessage[] mensajes = new SimpleMailMessage[emails.size()];

        for (int i = 0; i < emails.size(); i++) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(emails.get(i));
            msg.setSubject(asunto);
            msg.setText(mensaje);
            msg.setFrom("noreply@ciempies.com");
            mensajes[i] = msg;
        }

        // Enviar todos los correos
        try {
            mailSender.send(mensajes);
            System.out.println("✓ Correos enviados: " + emails.size() + " usuarios");
            return emails.size();
        } catch (Exception e) {
            System.err.println("✗ Error enviando correos: " + e.getMessage());
            throw new RuntimeException("Error al enviar correos: " + e.getMessage());
        }
    }
}