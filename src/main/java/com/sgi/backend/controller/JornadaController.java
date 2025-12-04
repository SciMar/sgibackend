package com.sgi.backend.controller;

import com.sgi.backend.dto.jornada.CrearJornadaDTO;
import com.sgi.backend.dto.jornada.ActualizarJornadaDTO;
import com.sgi.backend.dto.jornada.JornadaResponseDTO;
import com.sgi.backend.service.JornadaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
@RestController
@RequestMapping("/api/jornadas")
@CrossOrigin(origins = "*")
public class JornadaController {

    @Autowired
    private JornadaService jornadaService;

    // ==========================================
    // CREAR
    // ==========================================

    @PostMapping
    public ResponseEntity<JornadaResponseDTO> crear(@Valid @RequestBody CrearJornadaDTO dto) {
        try {
            JornadaResponseDTO jornada = jornadaService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(jornada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<JornadaResponseDTO>> listarTodas() {
        List<JornadaResponseDTO> jornadas = jornadaService.listarTodas();
        return ResponseEntity.ok(jornadas);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<JornadaResponseDTO>> listarActivas() {
        List<JornadaResponseDTO> jornadas = jornadaService.listarActivas();
        return ResponseEntity.ok(jornadas);
    }

    @GetMapping("/zona/{zonaId}")
    public ResponseEntity<List<JornadaResponseDTO>> listarPorZona(@PathVariable Long zonaId) {
        List<JornadaResponseDTO> jornadas = jornadaService.listarPorZona(zonaId);
        return ResponseEntity.ok(jornadas);
    }

    @GetMapping("/zona/{zonaId}/activas")
    public ResponseEntity<List<JornadaResponseDTO>> listarActivasPorZona(@PathVariable Long zonaId) {
        List<JornadaResponseDTO> jornadas = jornadaService.listarActivasPorZona(zonaId);
        return ResponseEntity.ok(jornadas);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<JornadaResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            JornadaResponseDTO jornada = jornadaService.obtenerPorId(id);
            return ResponseEntity.ok(jornada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo/{codigoJornada}")
    public ResponseEntity<JornadaResponseDTO> obtenerPorCodigo(@PathVariable String codigoJornada) {
        try {
            JornadaResponseDTO jornada = jornadaService.obtenerPorCodigo(codigoJornada);
            return ResponseEntity.ok(jornada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<JornadaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarJornadaDTO dto) {
        try {
            JornadaResponseDTO jornada = jornadaService.actualizar(id, dto);
            return ResponseEntity.ok(jornada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    @PatchMapping("/{id}/activar")
    public ResponseEntity<JornadaResponseDTO> activar(@PathVariable Long id) {
        try {
            JornadaResponseDTO jornada = jornadaService.activar(id);
            return ResponseEntity.ok(jornada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<JornadaResponseDTO> desactivar(@PathVariable Long id) {
        try {
            JornadaResponseDTO jornada = jornadaService.desactivar(id);
            return ResponseEntity.ok(jornada);
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
            jornadaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<JornadaResponseDTO> obtenerConEstadisticas(@PathVariable Long id) {
        try {
            JornadaResponseDTO jornada = jornadaService.obtenerConEstadisticas(id);
            return ResponseEntity.ok(jornada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
