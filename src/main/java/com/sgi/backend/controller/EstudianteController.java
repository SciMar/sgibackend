package com.sgi.backend.controller;

import com.sgi.backend.dto.estudiante.CrearEstudianteDTO;
import com.sgi.backend.dto.estudiante.ActualizarEstudianteDTO;
import com.sgi.backend.dto.estudiante.EstudianteResponseDTO;
import com.sgi.backend.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    // ==========================================
    // CREAR
    // ==========================================

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crear(@Valid @RequestBody CrearEstudianteDTO dto) {
        try {
            EstudianteResponseDTO estudiante = estudianteService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> listarTodos() {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarTodos();
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EstudianteResponseDTO>> listarActivos() {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarActivos();
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/colegio/{colegioId}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarPorColegio(@PathVariable Long colegioId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarPorColegio(colegioId);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/colegio/{colegioId}/activos")
    public ResponseEntity<List<EstudianteResponseDTO>> listarActivosPorColegio(@PathVariable Long colegioId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarActivosPorColegio(colegioId);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/jornada/{jornadaId}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarPorJornada(@PathVariable Long jornadaId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarPorJornada(jornadaId);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/colegio/{colegioId}/jornada/{jornadaId}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarPorColegioYJornada(
            @PathVariable Long colegioId,
            @PathVariable Long jornadaId) {
        List<EstudianteResponseDTO> estudiantes =
                estudianteService.listarPorColegioYJornada(colegioId, jornadaId);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/colegio/{colegioId}/jornada/{jornadaId}/activos")
    public ResponseEntity<List<EstudianteResponseDTO>> listarActivosPorColegioYJornada(
            @PathVariable Long colegioId,
            @PathVariable Long jornadaId) {
        List<EstudianteResponseDTO> estudiantes =
                estudianteService.listarActivosPorColegioYJornada(colegioId, jornadaId);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarPorRuta(@PathVariable Long rutaId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarPorRuta(rutaId);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/zona/{zonaId}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarPorZona(@PathVariable Long zonaId) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarPorZona(zonaId);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/estado/{estadoInscripcion}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarPorEstadoInscripcion(
            @PathVariable String estadoInscripcion) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarPorEstadoInscripcion(estadoInscripcion);
        return ResponseEntity.ok(estudiantes);
    }

    // ==========================================
    // PARA MONITORES (MUY IMPORTANTE)
    // ==========================================

    @GetMapping("/monitor/zona/{zonaId}/jornada/{jornadaId}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarEstudiantesParaMonitor(
            @PathVariable Long zonaId,
            @PathVariable Long jornadaId) {
        List<EstudianteResponseDTO> estudiantes =
                estudianteService.listarEstudiantesParaMonitor(zonaId, jornadaId);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/monitor/zona/{zonaId}/jornada/{jornadaId}/estado/{estado}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarEstudiantesParaMonitorPorEstado(
            @PathVariable Long zonaId,
            @PathVariable Long jornadaId,
            @PathVariable String estado) {
        List<EstudianteResponseDTO> estudiantes =
                estudianteService.listarEstudiantesParaMonitorPorEstado(zonaId, jornadaId, estado);
        return ResponseEntity.ok(estudiantes);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            EstudianteResponseDTO estudiante = estudianteService.obtenerPorId(id);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/documento/{numId}")
    public ResponseEntity<EstudianteResponseDTO> obtenerPorNumId(@PathVariable String numId) {
        try {
            EstudianteResponseDTO estudiante = estudianteService.obtenerPorNumId(numId);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EstudianteResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.buscarPorNombre(nombre);
        return ResponseEntity.ok(estudiantes);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstudianteDTO dto) {
        try {
            EstudianteResponseDTO estudiante = estudianteService.actualizar(id, dto);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    @PatchMapping("/{id}/activar")
    public ResponseEntity<EstudianteResponseDTO> activar(@PathVariable Long id) {
        try {
            EstudianteResponseDTO estudiante = estudianteService.activar(id);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<EstudianteResponseDTO> desactivar(@PathVariable Long id) {
        try {
            EstudianteResponseDTO estudiante = estudianteService.desactivar(id);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // CAMBIAR ESTADO DE INSCRIPCIÓN
    // ==========================================

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EstudianteResponseDTO> cambiarEstadoInscripcion(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("estado");
            String observaciones = body.get("observaciones");
            EstudianteResponseDTO estudiante =
                    estudianteService.cambiarEstadoInscripcion(id, nuevoEstado, observaciones);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/suspender")
    public ResponseEntity<EstudianteResponseDTO> suspender(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String motivo = body.get("motivo");
            EstudianteResponseDTO estudiante = estudianteService.suspender(id, motivo);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<EstudianteResponseDTO> finalizar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String motivo = body.get("motivo");
            EstudianteResponseDTO estudiante = estudianteService.finalizar(id, motivo);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<EstudianteResponseDTO> reactivar(@PathVariable Long id) {
        try {
            EstudianteResponseDTO estudiante = estudianteService.reactivar(id);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // CAMBIAR RUTA
    // ==========================================

    @PatchMapping("/{id}/ruta")
    public ResponseEntity<EstudianteResponseDTO> cambiarRuta(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        try {
            Long rutaId = body.get("rutaId");
            EstudianteResponseDTO estudiante = estudianteService.cambiarRuta(id, rutaId);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            estudianteService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS Y ASISTENCIAS
    // ==========================================

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<EstudianteResponseDTO> obtenerConEstadisticas(@PathVariable Long id) {
        try {
            EstudianteResponseDTO estudiante = estudianteService.obtenerConEstadisticas(id);
            return ResponseEntity.ok(estudiante);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/asistencias/contar")
    public ResponseEntity<Map<String, Long>> contarAsistencias(@PathVariable Long id) {
        Long total = estudianteService.contarAsistencias(id);
        Long presentes = estudianteService.contarAsistenciasPorEstado(id, "PRESENTE");
        Long ausentes = estudianteService.contarAsistenciasPorEstado(id, "AUSENTE");
        return ResponseEntity.ok(Map.of(
                "total", total,
                "presentes", presentes,
                "ausentes", ausentes
        ));
    }

    @GetMapping("/{id}/asistencias/rango")
    public ResponseEntity<Map<String, Long>> contarAsistenciasEnRango(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        Long total = estudianteService.contarAsistenciasEnRango(id, fechaInicio, fechaFin);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/{id}/porcentaje-asistencia")
    public ResponseEntity<Map<String, Double>> calcularPorcentajeAsistencia(@PathVariable Long id) {
        Double porcentaje = estudianteService.calcularPorcentajeAsistencia(id);
        return ResponseEntity.ok(Map.of("porcentaje", porcentaje));
    }

    // ==========================================
    // REPORTES POR FECHAS
    // ==========================================

    @GetMapping("/recientes/{dias}")
    public ResponseEntity<List<EstudianteResponseDTO>> listarInscritosRecientes(@PathVariable int dias) {
        List<EstudianteResponseDTO> estudiantes = estudianteService.listarInscritosRecientes(dias);
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<EstudianteResponseDTO>> listarInscritosEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<EstudianteResponseDTO> estudiantes =
                estudianteService.listarInscritosEnRango(fechaInicio, fechaFin);
        return ResponseEntity.ok(estudiantes);
    }

    // ==========================================
    // CONTADORES
    // ==========================================

    @GetMapping("/contar/colegio/{colegioId}")
    public ResponseEntity<Map<String, Long>> contarPorColegio(@PathVariable Long colegioId) {
        Long total = estudianteService.contarPorColegio(colegioId);
        Long activos = estudianteService.contarActivosPorColegio(colegioId);
        return ResponseEntity.ok(Map.of(
                "total", total,
                "activos", activos
        ));
    }

    @GetMapping("/contar/ruta/{rutaId}")
    public ResponseEntity<Map<String, Long>> contarPorRuta(@PathVariable Long rutaId) {
        Long total = estudianteService.contarPorRuta(rutaId);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/contar/estado/{estado}")
    public ResponseEntity<Map<String, Long>> contarPorEstadoInscripcion(@PathVariable String estado) {
        Long total = estudianteService.contarPorEstadoInscripcion(estado);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/contar/monitor")
    public ResponseEntity<Map<String, Long>> contarEstudiantesDeMonitor(
            @RequestParam Long zonaId,
            @RequestParam Long jornadaId) {
        Long total = estudianteService.contarEstudiantesDeMonitor(zonaId, jornadaId);
        return ResponseEntity.ok(Map.of("total", total));
    }
}