package com.sgi.backend.controller;

import com.sgi.backend.dto.zona.CrearZonaDTO;
import com.sgi.backend.dto.zona.ActualizarZonaDTO;
import com.sgi.backend.dto.zona.ZonaResponseDTO;
import com.sgi.backend.service.ZonaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
@RestController
@RequestMapping("/api/zonas")
@CrossOrigin(origins = "*")
public class ZonaController {

    @Autowired
    private ZonaService zonaService;

    // ==========================================
    // CREAR
    // ==========================================

    @PostMapping
    public ResponseEntity<ZonaResponseDTO> crear(@Valid @RequestBody CrearZonaDTO dto) {
        try {
            ZonaResponseDTO zona = zonaService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(zona);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<ZonaResponseDTO>> listarTodas() {
        List<ZonaResponseDTO> zonas = zonaService.listarTodas();
        return ResponseEntity.ok(zonas);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<ZonaResponseDTO>> listarActivas() {
        List<ZonaResponseDTO> zonas = zonaService.listarActivas();
        return ResponseEntity.ok(zonas);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<ZonaResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            ZonaResponseDTO zona = zonaService.obtenerPorId(id);
            return ResponseEntity.ok(zona);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo/{codigoZona}")
    public ResponseEntity<ZonaResponseDTO> obtenerPorCodigo(@PathVariable String codigoZona) {
        try {
            ZonaResponseDTO zona = zonaService.obtenerPorCodigo(codigoZona);
            return ResponseEntity.ok(zona);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ZonaResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<ZonaResponseDTO> zonas = zonaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(zonas);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<ZonaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarZonaDTO dto) {
        try {
            ZonaResponseDTO zona = zonaService.actualizar(id, dto);
            return ResponseEntity.ok(zona);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ZonaResponseDTO> activar(@PathVariable Long id) {
        try {
            ZonaResponseDTO zona = zonaService.activar(id);
            return ResponseEntity.ok(zona);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ZonaResponseDTO> desactivar(@PathVariable Long id) {
        try {
            ZonaResponseDTO zona = zonaService.desactivar(id);
            return ResponseEntity.ok(zona);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            zonaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<ZonaResponseDTO> obtenerConEstadisticas(@PathVariable Long id) {
        try {
            ZonaResponseDTO zona = zonaService.obtenerConEstadisticas(id);
            return ResponseEntity.ok(zona);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
