package com.sgi.backend.dto.monitor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarMonitorDTO {

    @NotNull(message = "La zona es obligatoria")
    private Long zonaId;

    @NotNull(message = "La jornada es obligatoria")
    private Long jornadaId;
}