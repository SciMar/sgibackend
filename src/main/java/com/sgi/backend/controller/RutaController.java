package com.sgi.backend.controller;

import com.sgi.backend.dto.ruta.CrearRutaDTO;
import com.sgi.backend.dto.ruta.ActualizarRutaDTO;
import com.sgi.backend.dto.ruta.RutaResponseDTO;
import com.sgi.backend.model.TipoRecorrido;
import com.sgi.backend.service.RutaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rutas")
@CrossOrigin(origins = "*")
public class RutaController {

    @Autowired
    private RutaService rutaService;

    // ==========================================
    // CREAR
    // ==========================================

    @PostMapping
    public ResponseEntity<RutaResponseDTO> crear(@Valid @RequestBody CrearRutaDTO dto) {
        try {
            RutaResponseDTO ruta = rutaService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ruta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<RutaResponseDTO>> listarTodas() {
        List<RutaResponseDTO> rutas = rutaService.listarTodas();
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<RutaResponseDTO>> listarActivas() {
        List<RutaResponseDTO> rutas = rutaService.listarActivas();
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/zona/{zonaId}")
    public ResponseEntity<List<RutaResponseDTO>> listarPorZona(@PathVariable Long zonaId) {
        List<RutaResponseDTO> rutas = rutaService.listarPorZona(zonaId);
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/zona/{zonaId}/activas")
    public ResponseEntity<List<RutaResponseDTO>> listarActivasPorZona(@PathVariable Long zonaId) {
        List<RutaResponseDTO> rutas = rutaService.listarActivasPorZona(zonaId);
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/tipo/{tipoRecorrido}")
    public ResponseEntity<List<RutaResponseDTO>> listarPorTipo(@PathVariable TipoRecorrido tipoRecorrido) {
        List<RutaResponseDTO> rutas = rutaService.listarPorTipo(tipoRecorrido);
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/tipo/{tipoRecorrido}/activas")
    public ResponseEntity<List<RutaResponseDTO>> listarActivasPorTipo(@PathVariable TipoRecorrido tipoRecorrido) {
        List<RutaResponseDTO> rutas = rutaService.listarActivasPorTipo(tipoRecorrido);
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/zona/{zonaId}/tipo/{tipoRecorrido}")
    public ResponseEntity<List<RutaResponseDTO>> listarPorZonaYTipo(
            @PathVariable Long zonaId,
            @PathVariable TipoRecorrido tipoRecorrido) {
        List<RutaResponseDTO> rutas = rutaService.listarPorZonaYTipo(zonaId, tipoRecorrido);
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/zona/{zonaId}/tipo/{tipoRecorrido}/activas")
    public ResponseEntity<List<RutaResponseDTO>> listarActivasPorZonaYTipo(
            @PathVariable Long zonaId,
            @PathVariable TipoRecorrido tipoRecorrido) {
        List<RutaResponseDTO> rutas = rutaService.listarActivasPorZonaYTipo(zonaId, tipoRecorrido);
        return ResponseEntity.ok(rutas);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<RutaResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            RutaResponseDTO ruta = rutaService.obtenerPorId(id);
            return ResponseEntity.ok(ruta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<RutaResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<RutaResponseDTO> rutas = rutaService.buscarPorNombre(nombre);
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/buscar/zona/{zonaId}")
    public ResponseEntity<List<RutaResponseDTO>> buscarPorNombreEnZona(
            @RequestParam String nombre,
            @PathVariable Long zonaId) {
        List<RutaResponseDTO> rutas = rutaService.buscarPorNombreEnZona(nombre, zonaId);
        return ResponseEntity.ok(rutas);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<RutaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarRutaDTO dto) {
        try {
            RutaResponseDTO ruta = rutaService.actualizar(id, dto);
            return ResponseEntity.ok(ruta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    @PatchMapping("/{id}/activar")
    public ResponseEntity<RutaResponseDTO> activar(@PathVariable Long id) {
        try {
            RutaResponseDTO ruta = rutaService.activar(id);
            return ResponseEntity.ok(ruta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<RutaResponseDTO> desactivar(@PathVariable Long id) {
        try {
            RutaResponseDTO ruta = rutaService.desactivar(id);
            return ResponseEntity.ok(ruta);
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
            rutaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<RutaResponseDTO> obtenerConEstadisticas(@PathVariable Long id) {
        try {
            RutaResponseDTO ruta = rutaService.obtenerConEstadisticas(id);
            return ResponseEntity.ok(ruta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/contadores")
    public ResponseEntity<Map<String, Long>> obtenerContadores(@PathVariable Long id) {
        Long totalEstudiantes = rutaService.contarEstudiantes(id);
        Long estudiantesActivos = rutaService.contarEstudiantesActivos(id);
        return ResponseEntity.ok(Map.of(
                "totalEstudiantes", totalEstudiantes,
                "estudiantesActivos", estudiantesActivos
        ));
    }

    // ==========================================
    // OPERACIONES ESPECIALES
    // ==========================================

    @GetMapping("/zona/{zonaId}/ida")
    public ResponseEntity<List<RutaResponseDTO>> obtenerRutasIda(@PathVariable Long zonaId) {
        List<RutaResponseDTO> rutas = rutaService.obtenerRutasIda(zonaId);
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/zona/{zonaId}/regreso")
    public ResponseEntity<List<RutaResponseDTO>> obtenerRutasRegreso(@PathVariable Long zonaId) {
        List<RutaResponseDTO> rutas = rutaService.obtenerRutasRegreso(zonaId);
        return ResponseEntity.ok(rutas);
    }

    @PostMapping("/{id}/clonar")
    public ResponseEntity<RutaResponseDTO> clonar(
            @PathVariable Long id,
            @RequestParam TipoRecorrido nuevoTipo) {
        try {
            RutaResponseDTO ruta = rutaService.clonar(id, nuevoTipo);
            return ResponseEntity.status(HttpStatus.CREATED).body(ruta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}