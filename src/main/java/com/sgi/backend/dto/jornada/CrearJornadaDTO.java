package com.sgi.backend.dto.jornada;

import com.sgi.backend.model.TipoJornada;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearJornadaDTO {

    @NotBlank(message = "El código de jornada es obligatorio")
    @Size(max = 50)
    private String codigoJornada;

    @NotNull(message = "El tipo de jornada es obligatorio")
    private TipoJornada nombreJornada;

    @NotNull(message = "La zona es obligatoria")
    private Long zonaId;
}
