package com.sgi.backend.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupMessage {

    @EventListener(ApplicationReadyEvent.class)
    public void mostrarMensajeInicio() {
        System.out.println("");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  🐛 SISTEMA CIEMPIÉS - SERVIDOR INICIADO CORRECTAMENTE");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        System.out.println("");
        System.out.println("  📡 API Backend:");
        System.out.println("     → http://localhost:8080/api");
        System.out.println("");
        System.out.println("  📖 Documentación Swagger:");
        System.out.println("     → http://localhost:8080/swagger-ui.html");
        System.out.println("");
        System.out.println("  🌐 Frontend:");
        System.out.println("     → http://localhost:8080/index.html");
        System.out.println("");
        System.out.println("  🔑 Credenciales de acceso:");
        System.out.println("     → Admin: admin@ciempies.com / Ciempies2024!");
        System.out.println("     → Otros usuarios: [email] / Ciempies2024!");
        System.out.println("       (Deben cambiar contraseña en el primer ingreso)");
        System.out.println("");
        System.out.println("═══════════════════════════════════════════════════════════════════");
        System.out.println("");
    }
}
