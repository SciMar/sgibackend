package com.sgi.backend.controller;

import com.sgi.backend.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @PostMapping("/enviar-masivo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<?> enviarCorreoMasivo(@RequestBody Map<String, String> request) {
        try {
            String asunto = request.get("asunto");
            String mensaje = request.get("mensaje");

            int enviados = notificacionService.enviarCorreoMasivo(asunto, mensaje);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Correos enviados exitosamente a todos los usuarios",
                    "destinatarios", enviados
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}
