package com.sgi.backend.controller;

import com.sgi.backend.dto.clima.ClimaDTO;
import com.sgi.backend.service.ClimaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clima")
@CrossOrigin(origins = "*")
public class ClimaController {

    @Autowired
    private ClimaService climaService;

    @GetMapping("/actual")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO', 'MONITOR')")
    public ResponseEntity<ClimaDTO> obtenerClimaActual() {
        try {
            ClimaDTO clima = climaService.obtenerClimaBogota();
            return ResponseEntity.ok(clima);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/recomendacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO', 'MONITOR')")
    public ResponseEntity<?> obtenerRecomendacion() {
        try {
            ClimaDTO clima = climaService.obtenerClimaBogota();
            String recomendacion = climaService.obtenerRecomendacion();

            return ResponseEntity.ok(Map.of(
                    "clima", clima,
                    "recomendacion", recomendacion
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/alertas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<?> verificarAlertas() {
        try {
            boolean hayAlertas = climaService.hayCondicionesAdversas();
            ClimaDTO clima = climaService.obtenerClimaBogota();

            return ResponseEntity.ok(Map.of(
                    "hayAlertas", hayAlertas,
                    "nivelAlerta", clima.getAlerta(),
                    "descripcion", clima.getDescripcion(),
                    "temperatura", clima.getTemperatura(),
                    "recomendacion", climaService.obtenerRecomendacion()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
