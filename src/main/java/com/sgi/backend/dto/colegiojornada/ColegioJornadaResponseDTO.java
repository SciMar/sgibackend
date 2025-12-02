package com.sgi.backend.dto.colegiojornada;

import lombok.Data;

@Data
public class ColegioJornadaResponseDTO {
    private Long id;
    private Boolean activa;

    // Datos del colegio
    private Long colegioId;
    private String nombreColegio;

    // Datos de la jornada
    private Long jornadaId;
    private String nombreJornada;
}
