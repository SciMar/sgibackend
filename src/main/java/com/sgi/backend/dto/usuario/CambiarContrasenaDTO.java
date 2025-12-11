package com.sgi.backend.dto.usuario;

import com.sgi.backend.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para cambiar la contraseña de un usuario.
 */
public class CambiarContrasenaDTO {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String contrasenaActual;

    /**
     * Nueva contraseña con validación robusta:
     * - Mínimo 8 caracteres
     * - Al menos una mayúscula
     * - Al menos una minúscula
     * - Al menos un número
     * - Al menos un carácter especial (@#$%^&+=!.*_-)
     * - Sin espacios en blanco
     */
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @ValidPassword
    private String contrasenaNueva;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmarContrasena;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getContrasenaActual() {
        return contrasenaActual;
    }

    public void setContrasenaActual(String contrasenaActual) {
        this.contrasenaActual = contrasenaActual;
    }

    public String getContrasenaNueva() {
        return contrasenaNueva;
    }

    public void setContrasenaNueva(String contrasenaNueva) {
        this.contrasenaNueva = contrasenaNueva;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }

    /**
     * Valida que la nueva contraseña y la confirmación coincidan.
     */
    public boolean contrasenaCoincide() {
        return contrasenaNueva != null && contrasenaNueva.equals(confirmarContrasena);
    }
}
