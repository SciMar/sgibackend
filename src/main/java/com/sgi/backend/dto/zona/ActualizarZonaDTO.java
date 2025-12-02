package com.sgi.backend.dto.zona;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarZonaDTO {

    @NotBlank(message = "El código de zona es obligatorio")
    @Size(max = 50)
    private String codigoZona;

    @NotBlank(message = "El nombre de zona es obligatorio")
    @Size(max = 200)
    private String nombreZona;

    @Size(max = 500)
    private String descripcion;
}
