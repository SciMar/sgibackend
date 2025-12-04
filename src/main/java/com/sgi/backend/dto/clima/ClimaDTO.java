package com.sgi.backend.dto.clima;

import lombok.Data;

@Data
public class ClimaDTO {
    private String ciudad;
    private String descripcion;
    private Double temperatura;
    private Integer humedad;
    private Double sensacionTermica;
    private String estado; // "despejado", "lluvia", "nublado", etc.
    private String alerta; // "NORMAL", "PRECAUCION", "ALERTA"
}
