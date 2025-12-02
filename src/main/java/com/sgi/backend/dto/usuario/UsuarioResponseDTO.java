package com.sgi.backend.dto.usuario;

import com.sgi.backend.model.Rol;
import com.sgi.backend.model.TipoId;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private TipoId tipoId;
    private String numId;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String email;
    private Rol rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    // SIN contraseña (seguridad)
}
