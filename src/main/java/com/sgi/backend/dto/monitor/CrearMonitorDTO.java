package com.sgi.backend.dto.monitor;

import com.sgi.backend.model.Rol;
import com.sgi.backend.model.TipoId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CrearMonitorDTO {

    // Datos del usuario
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

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    // Asignación del monitor
    @NotNull(message = "La zona es obligatoria")
    private Long zonaId;

    @NotNull(message = "La jornada es obligatoria")
    private Long jornadaId;
}