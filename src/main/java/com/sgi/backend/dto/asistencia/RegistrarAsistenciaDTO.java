package com.sgi.backend.dto.asistencia;

import com.sgi.backend.model.EstadoAsistencia;
import com.sgi.backend.model.TipoRecorrido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegistrarAsistenciaDTO {

    @NotNull(message = "El estudiante es obligatorio")
    private Long estudianteId;

    @NotNull(message = "El tipo de recorrido es obligatorio")
    private TipoRecorrido tipoRecorrido;

    @NotNull(message = "El estado de asistencia es obligatorio")
    private EstadoAsistencia estadoAsistencia;

    private LocalDate fecha; // Si no se envía, se usa la fecha actual

    @Size(max = 500)
    private String observaciones;
}
