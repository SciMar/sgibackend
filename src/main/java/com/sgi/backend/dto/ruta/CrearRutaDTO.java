package com.sgi.backend.dto.ruta;

import com.sgi.backend.model.TipoRecorrido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearRutaDTO {

    @NotNull(message = "La jornada del colegio es obligatoria")
    private Long colegioJornadaId;

    @NotNull(message = "El tipo de recorrido es obligatorio")
    private TipoRecorrido tipoRuta;
}