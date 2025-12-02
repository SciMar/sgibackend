package com.sgi.backend.dto.zona;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearZonaDTO {

    @NotBlank(message = "El código de zona es obligatorio")
    @Size(max = 50, message = "El código no puede superar 50 caracteres")
    private String codigoZona;

    @NotBlank(message = "El nombre de zona es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombreZona;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;
}