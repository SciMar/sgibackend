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
    private String tipo = "Bearer";
}
