package com.sgi.backend.dto.colegio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarColegioDTO {

    @NotBlank(message = "El nombre del colegio es obligatorio")
    @Size(max = 200)
    private String nombreColegio;
}
