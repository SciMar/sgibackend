package com.sgi.backend.controller;

import com.sgi.backend.dto.monitor.CrearMonitorDTO;
import com.sgi.backend.dto.monitor.ActualizarMonitorDTO;
import com.sgi.backend.dto.monitor.MonitorResponseDTO;
import com.sgi.backend.service.MonitorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
@RestController
@RequestMapping("/api/monitores")
@CrossOrigin(origins = "*")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    // ==========================================
    // CREAR
    // ==========================================

    @PostMapping
    public ResponseEntity<MonitorResponseDTO> crear(@Valid @RequestBody CrearMonitorDTO dto) {
        try {
            MonitorResponseDTO monitor = monitorService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(monitor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<MonitorResponseDTO>> listarTodos() {
        List<MonitorResponseDTO> monitores = monitorService.listarTodos();
        return ResponseEntity.ok(monitores);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<MonitorResponseDTO>> listarActivos() {
        List<MonitorResponseDTO> monitores = monitorService.listarActivos();
        return ResponseEntity.ok(monitores);
    }

    @GetMapping("/zona/{zonaId}")
    public ResponseEntity<List<MonitorResponseDTO>> listarPorZona(@PathVariable Long zonaId) {
        List<MonitorResponseDTO> monitores = monitorService.listarPorZona(zonaId);
        return ResponseEntity.ok(monitores);
    }

    @GetMapping("/zona/{zonaId}/activos")
    public ResponseEntity<List<MonitorResponseDTO>> listarActivosPorZona(@PathVariable Long zonaId) {
        List<MonitorResponseDTO> monitores = monitorService.listarActivosPorZona(zonaId);
        return ResponseEntity.ok(monitores);
    }

    @GetMapping("/zona/{zonaId}/jornada/{jornadaId}")
    public ResponseEntity<List<MonitorResponseDTO>> listarPorZonaYJornada(
            @PathVariable Long zonaId,
            @PathVariable Long jornadaId) {
        List<MonitorResponseDTO> monitores = monitorService.listarPorZonaYJornada(zonaId, jornadaId);
        return ResponseEntity.ok(monitores);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<MonitorResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            MonitorResponseDTO monitor = monitorService.obtenerPorId(id);
            return ResponseEntity.ok(monitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO', 'MONITOR')")
    public ResponseEntity<MonitorResponseDTO> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        try {
            MonitorResponseDTO monitor = monitorService.obtenerPorUsuarioId(usuarioId);
            return ResponseEntity.ok(monitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<MonitorResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarMonitorDTO dto) {
        try {
            MonitorResponseDTO monitor = monitorService.actualizar(id, dto);
            return ResponseEntity.ok(monitor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // REASIGNAR
    // ==========================================

    @PostMapping("/{id}/reasignar")
    public ResponseEntity<MonitorResponseDTO> reasignar(
            @PathVariable Long id,
            @RequestParam Long zonaId,
            @RequestParam Long jornadaId) {
        try {
            MonitorResponseDTO monitor = monitorService.reasignar(id, zonaId, jornadaId);
            return ResponseEntity.ok(monitor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    @PatchMapping("/{id}/activar")
    public ResponseEntity<MonitorResponseDTO> activar(@PathVariable Long id) {
        try {
            MonitorResponseDTO monitor = monitorService.activar(id);
            return ResponseEntity.ok(monitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<MonitorResponseDTO> desactivar(@PathVariable Long id) {
        try {
            MonitorResponseDTO monitor = monitorService.desactivar(id);
            return ResponseEntity.ok(monitor);
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
            monitorService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<MonitorResponseDTO> obtenerConEstadisticas(@PathVariable Long id) {
        try {
            MonitorResponseDTO monitor = monitorService.obtenerConEstadisticas(id);
            return ResponseEntity.ok(monitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // BÚSQUEDAS POR FECHA
    // ==========================================

    @GetMapping("/recientes/{dias}")
    public ResponseEntity<List<MonitorResponseDTO>> listarAsignadosRecientes(@PathVariable int dias) {
        List<MonitorResponseDTO> monitores = monitorService.listarAsignadosRecientes(dias);
        return ResponseEntity.ok(monitores);
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<MonitorResponseDTO>> listarAsignadosEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<MonitorResponseDTO> monitores = monitorService.listarAsignadosEnRango(fechaInicio, fechaFin);
        return ResponseEntity.ok(monitores);
    }
}
