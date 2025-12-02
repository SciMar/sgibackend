package com.sgi.backend.dto.zona;

import lombok.Data;

@Data
public class ZonaResponseDTO {
    private Long id;
    private String codigoZona;
    private String nombreZona;
    private String descripcion;
    private Boolean activa;

    // Estadísticas opcionales
    private Long totalColegios;
    private Long totalMonitores;
    private Long totalJornadas;
    private Long totalRutas;
}
