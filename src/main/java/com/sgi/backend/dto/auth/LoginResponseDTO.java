package com.sgi.backend.dto.auth;

import com.sgi.backend.dto.usuario.UsuarioResponseDTO;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private UsuarioResponseDTO usuario;
    private String token;
    private String tokenType = "Bearer";

    /**
     * Indica si el usuario debe cambiar su contraseña antes de continuar.
     * TRUE = Redirigir a pantalla de cambio de contraseña
     * FALSE = Puede acceder al sistema normalmente
     */
    private Boolean requiereCambioContrasena;

    // Constructor compatible con versiones anteriores (sin requiereCambioContrasena explícito)
    public LoginResponseDTO(UsuarioResponseDTO usuario, String token, String tokenType) {
        this.usuario = usuario;
        this.token = token;
        this.tokenType = tokenType;
        this.requiereCambioContrasena = usuario.getPrimerIngreso();
    }
}

