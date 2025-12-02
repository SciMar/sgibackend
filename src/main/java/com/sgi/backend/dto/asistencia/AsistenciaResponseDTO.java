package com.sgi.backend.dto.asistencia;

import com.sgi.backend.model.EstadoAsistencia;
import com.sgi.backend.model.TipoRecorrido;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AsistenciaResponseDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime horaRegistro;
    private TipoRecorrido tipoRecorrido;
    private EstadoAsistencia estadoAsistencia;
    private String observaciones;

    // Datos del estudiante (sin toda la entidad)
    private Long estudianteId;
    private String nombreEstudiante;
    private String numIdEstudiante;

    // Datos del colegio
    private Long colegioId;
    private String nombreColegio;

    // Datos del monitor que registró
    private Long monitorId;
    private String nombreMonitor;
}