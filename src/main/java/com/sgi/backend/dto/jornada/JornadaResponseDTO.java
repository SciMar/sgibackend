package com.sgi.backend.dto.jornada;

import com.sgi.backend.model.TipoJornada;
import lombok.Data;

@Data
public class JornadaResponseDTO {
    private Long id;
    private String codigoJornada;
    private TipoJornada nombreJornada;
    private Boolean activa;

    // Datos de zona (sin toda la entidad)
    private Long zonaId;
    private String nombreZona;

    // Estadísticas opcionales
    private Long totalEstudiantes;
    private Long totalMonitores;
}
