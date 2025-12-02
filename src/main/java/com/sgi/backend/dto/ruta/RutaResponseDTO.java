package com.sgi.backend.dto.ruta;

import com.sgi.backend.model.TipoRecorrido;
import lombok.Data;

@Data
public class RutaResponseDTO {
    private Long id;
    private String nombreRuta;
    private TipoRecorrido tipoRuta;
    private Boolean activa;

    // Datos de zona
    private Long zonaId;
    private String nombreZona;

    // Estadísticas opcionales
    private Long totalEstudiantes;
}
