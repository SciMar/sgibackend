package com.sgi.backend.dto.ruta;

import com.sgi.backend.model.TipoRecorrido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearRutaDTO {

    @NotBlank(message = "El nombre de la ruta es obligatorio")
    @Size(max = 200)
    private String nombreRuta;

    @NotNull(message = "El tipo de ruta es obligatorio")
    private TipoRecorrido tipoRuta;

    @NotNull(message = "La zona es obligatoria")
    private Long zonaId;
}
