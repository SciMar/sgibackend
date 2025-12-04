package com.sgi.backend.controller;

import com.sgi.backend.dto.colegio.CrearColegioDTO;
import com.sgi.backend.dto.colegio.ActualizarColegioDTO;
import com.sgi.backend.dto.colegio.ColegioResponseDTO;
import com.sgi.backend.service.ColegioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
@RestController
@RequestMapping("/api/colegios")
@CrossOrigin(origins = "*")
public class ColegioController {

    @Autowired
    private ColegioService colegioService;

    // ==========================================
    // CREAR
    // ==========================================

    @PostMapping
    public ResponseEntity<ColegioResponseDTO> crear(@Valid @RequestBody CrearColegioDTO dto) {
        try {
            ColegioResponseDTO colegio = colegioService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(colegio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<ColegioResponseDTO>> listarTodos() {
        List<ColegioResponseDTO> colegios = colegioService.listarTodos();
        return ResponseEntity.ok(colegios);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ColegioResponseDTO>> listarActivos() {
        List<ColegioResponseDTO> colegios = colegioService.listarActivos();
        return ResponseEntity.ok(colegios);
    }

    @GetMapping("/zona/{zonaId}")
    public ResponseEntity<List<ColegioResponseDTO>> listarPorZona(@PathVariable Long zonaId) {
        List<ColegioResponseDTO> colegios = colegioService.listarPorZona(zonaId);
        return ResponseEntity.ok(colegios);
    }

    @GetMapping("/zona/{zonaId}/activos")
    public ResponseEntity<List<ColegioResponseDTO>> listarActivosPorZona(@PathVariable Long zonaId) {
        List<ColegioResponseDTO> colegios = colegioService.listarActivosPorZona(zonaId);
        return ResponseEntity.ok(colegios);
    }

    @GetMapping("/jornada/{jornadaId}")
    public ResponseEntity<List<ColegioResponseDTO>> listarPorJornada(@PathVariable Long jornadaId) {
        List<ColegioResponseDTO> colegios = colegioService.listarPorJornada(jornadaId);
        return ResponseEntity.ok(colegios);
    }

    @GetMapping("/jornada/{jornadaId}/activos")
    public ResponseEntity<List<ColegioResponseDTO>> listarActivosPorJornada(@PathVariable Long jornadaId) {
        List<ColegioResponseDTO> colegios = colegioService.listarActivosPorJornada(jornadaId);
        return ResponseEntity.ok(colegios);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<ColegioResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            ColegioResponseDTO colegio = colegioService.obtenerPorId(id);
            return ResponseEntity.ok(colegio);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ColegioResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<ColegioResponseDTO> colegios = colegioService.buscarPorNombre(nombre);
        return ResponseEntity.ok(colegios);
    }

    @GetMapping("/buscar/zona/{zonaId}")
    public ResponseEntity<List<ColegioResponseDTO>> buscarPorNombreEnZona(
            @RequestParam String nombre,
            @PathVariable Long zonaId) {
        List<ColegioResponseDTO> colegios = colegioService.buscarPorNombreEnZona(nombre, zonaId);
        return ResponseEntity.ok(colegios);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<ColegioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarColegioDTO dto) {
        try {
            ColegioResponseDTO colegio = colegioService.actualizar(id, dto);
            return ResponseEntity.ok(colegio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ColegioResponseDTO> activar(@PathVariable Long id) {
        try {
            ColegioResponseDTO colegio = colegioService.activar(id);
            return ResponseEntity.ok(colegio);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ColegioResponseDTO> desactivar(@PathVariable Long id) {
        try {
            ColegioResponseDTO colegio = colegioService.desactivar(id);
            return ResponseEntity.ok(colegio);
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
            colegioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<ColegioResponseDTO> obtenerConEstadisticas(@PathVariable Long id) {
        try {
            ColegioResponseDTO colegio = colegioService.obtenerConEstadisticas(id);
            return ResponseEntity.ok(colegio);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/contadores")
    public ResponseEntity<Map<String, Long>> obtenerContadores(@PathVariable Long id) {
        Long totalEstudiantes = colegioService.contarEstudiantes(id);
        Long estudiantesActivos = colegioService.contarEstudiantesActivos(id);
        return ResponseEntity.ok(Map.of(
                "totalEstudiantes", totalEstudiantes,
                "estudiantesActivos", estudiantesActivos
        ));
    }
}
