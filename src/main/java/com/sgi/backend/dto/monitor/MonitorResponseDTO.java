package com.sgi.backend.dto.monitor;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MonitorResponseDTO {
    private Long id;
    private LocalDate fechaAsignacion;
    private Boolean activo;

    // Datos del usuario
    private Long usuarioId;
    private String nombreCompleto;
    private String email;

    // Datos de zona y jornada (sin toda la entidad)
    private Long zonaId;
    private String nombreZona;
    private Long jornadaId;
    private String nombreJornada;

    // Estadísticas opcionales
    private Long totalAsistenciasRegistradas;
}
