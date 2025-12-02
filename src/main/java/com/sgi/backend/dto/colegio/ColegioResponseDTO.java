package com.sgi.backend.dto.colegio;

import lombok.Data;
import java.util.List;

@Data
public class ColegioResponseDTO {
    private Long id;
    private String nombreColegio;
    private Boolean activo;

    // Datos de zona
    private Long zonaId;
    private String nombreZona;

    // Jornadas del colegio (IDs y nombres)
    private List<JornadaInfo> jornadas;

    // Estadísticas opcionales
    private Long totalEstudiantes;

    @Data
    public static class JornadaInfo {
        private Long id;
        private String nombre;
    }
}