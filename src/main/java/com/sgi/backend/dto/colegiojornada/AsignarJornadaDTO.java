package com.sgi.backend.dto.colegiojornada;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarJornadaDTO {

    @NotNull(message = "El colegio es obligatorio")
    private Long colegioId;

    @NotNull(message = "La jornada es obligatoria")
    private Long jornadaId;
}
