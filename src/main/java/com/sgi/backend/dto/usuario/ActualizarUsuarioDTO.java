package com.sgi.backend.dto.usuario;

import com.sgi.backend.model.TipoId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarUsuarioDTO {

    private TipoId tipoId;

    @NotBlank(message = "El número de identificación es obligatorio")
    @Size(max = 50)
    private String numId;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 100)
    private String primerNombre;

    @Size(max = 100)
    private String segundoNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 100)
    private String primerApellido;

    @Size(max = 100)
    private String segundoApellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Size(max = 150)
    private String email;

    // Contraseña opcional (solo si se quiere cambiar)
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;
}