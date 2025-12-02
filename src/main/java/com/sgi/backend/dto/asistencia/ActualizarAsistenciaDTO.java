package com.sgi.backend.dto.asistencia;

import com.sgi.backend.model.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarAsistenciaDTO {

    @NotNull(message = "El estado de asistencia es obligatorio")
    private EstadoAsistencia estadoAsistencia;

    @Size(max = 500)
    private String observaciones;
}
