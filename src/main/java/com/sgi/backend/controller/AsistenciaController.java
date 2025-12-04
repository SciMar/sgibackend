package com.sgi.backend.controller;

import com.sgi.backend.dto.asistencia.RegistrarAsistenciaDTO;
import com.sgi.backend.dto.asistencia.ActualizarAsistenciaDTO;
import com.sgi.backend.dto.asistencia.AsistenciaResponseDTO;
import com.sgi.backend.model.EstadoAsistencia;
import com.sgi.backend.model.TipoRecorrido;
import com.sgi.backend.service.AsistenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO', 'MONITOR')")
@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    // ==========================================
    // REGISTRAR ASISTENCIA
    // ==========================================

    @PostMapping
    public ResponseEntity<AsistenciaResponseDTO> registrar(
            @Valid @RequestBody RegistrarAsistenciaDTO dto,
            @RequestParam Long monitorId) {
        try {
            AsistenciaResponseDTO asistencia = asistenciaService.registrar(dto, monitorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(asistencia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ACTUALIZAR ASISTENCIA
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarAsistenciaDTO dto,
            @RequestParam Long monitorId) {
        try {
            AsistenciaResponseDTO asistencia = asistenciaService.actualizar(id, dto, monitorId);
            return ResponseEntity.ok(asistencia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<AsistenciaResponseDTO>> listarTodas() {
        List<AsistenciaResponseDTO> asistencias = asistenciaService.listarTodas();
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarDeHoy() {
        List<AsistenciaResponseDTO> asistencias = asistenciaService.listarDeHoy();
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsistenciaResponseDTO> asistencias = asistenciaService.listarPorFecha(fecha);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<AsistenciaResponseDTO> asistencias =
                asistenciaService.listarPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(asistencias);
    }

    // ==========================================
    // LISTAR POR ESTUDIANTE
    // ==========================================

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorEstudiante(@PathVariable Long estudianteId) {
        List<AsistenciaResponseDTO> asistencias = asistenciaService.listarPorEstudiante(estudianteId);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/estudiante/{estudianteId}/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorEstudianteYFecha(
            @PathVariable Long estudianteId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsistenciaResponseDTO> asistencias =
                asistenciaService.listarPorEstudianteYFecha(estudianteId, fecha);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/estudiante/{estudianteId}/rango")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorEstudianteEnRango(
            @PathVariable Long estudianteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<AsistenciaResponseDTO> asistencias =
                asistenciaService.listarPorEstudianteEnRango(estudianteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(asistencias);
    }

    // ==========================================
    // LISTAR POR COLEGIO
    // ==========================================

    @GetMapping("/colegio/{colegioId}/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorColegio(
            @PathVariable Long colegioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsistenciaResponseDTO> asistencias = asistenciaService.listarPorColegio(colegioId, fecha);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/colegio/{colegioId}/rango")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorColegioEnRango(
            @PathVariable Long colegioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<AsistenciaResponseDTO> asistencias =
                asistenciaService.listarPorColegioEnRango(colegioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(asistencias);
    }

    // ==========================================
    // LISTAR POR MONITOR
    // ==========================================

    @GetMapping("/monitor/{monitorId}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorMonitor(@PathVariable Long monitorId) {
        List<AsistenciaResponseDTO> asistencias = asistenciaService.listarPorMonitor(monitorId);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/monitor/{monitorId}/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorMonitorYFecha(
            @PathVariable Long monitorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsistenciaResponseDTO> asistencias =
                asistenciaService.listarPorMonitorYFecha(monitorId, fecha);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/monitor/{monitorId}/rango")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorMonitorEnRango(
            @PathVariable Long monitorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<AsistenciaResponseDTO> asistencias =
                asistenciaService.listarPorMonitorEnRango(monitorId, fechaInicio, fechaFin);
        return ResponseEntity.ok(asistencias);
    }

    // ==========================================
    // LISTAR POR ZONA
    // ==========================================

    @GetMapping("/zona/{zonaId}/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorZona(
            @PathVariable Long zonaId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsistenciaResponseDTO> asistencias = asistenciaService.listarPorZona(zonaId, fecha);
        return ResponseEntity.ok(asistencias);
    }

    // ==========================================
    // FILTROS POR TIPO Y ESTADO
    // ==========================================

    @GetMapping("/tipo/{tipoRecorrido}/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorTipoRecorrido(
            @PathVariable TipoRecorrido tipoRecorrido,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsistenciaResponseDTO> asistencias =
                asistenciaService.listarPorTipoRecorrido(tipoRecorrido, fecha);
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/estado/{estadoAsistencia}/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaResponseDTO>> listarPorEstadoAsistencia(
            @PathVariable EstadoAsistencia estadoAsistencia,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsistenciaResponseDTO> asistencias =
                asistenciaService.listarPorEstadoAsistencia(estadoAsistencia, fecha);
        return ResponseEntity.ok(asistencias);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            AsistenciaResponseDTO asistencia = asistenciaService.obtenerPorId(id);
            return ResponseEntity.ok(asistencia);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estudiante/{estudianteId}/fecha/{fecha}/tipo/{tipoRecorrido}")
    public ResponseEntity<AsistenciaResponseDTO> obtenerPorEstudianteFechaYTipo(
            @PathVariable Long estudianteId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @PathVariable TipoRecorrido tipoRecorrido) {
        try {
            AsistenciaResponseDTO asistencia =
                    asistenciaService.obtenerPorEstudianteFechaYTipo(estudianteId, fecha, tipoRecorrido);
            return ResponseEntity.ok(asistencia);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestParam Long monitorId) {
        try {
            asistenciaService.eliminar(id, monitorId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS GENERALES
    // ==========================================

    @GetMapping("/estadisticas/hoy")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticasDeHoy() {
        Map<String, Long> stats = asistenciaService.obtenerEstadisticasDeHoy();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/estadisticas/fecha/{fecha}")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticasPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Map<String, Long> stats = asistenciaService.obtenerEstadisticasPorFecha(fecha);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/estadisticas/colegio/{colegioId}/fecha/{fecha}")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticasPorColegio(
            @PathVariable Long colegioId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Map<String, Long> stats = asistenciaService.obtenerEstadisticasPorColegio(colegioId, fecha);
        return ResponseEntity.ok(stats);
    }

    // ==========================================
    // ESTADÍSTICAS POR ESTUDIANTE
    // ==========================================

    @GetMapping("/estadisticas/estudiante/{estudianteId}")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasEstudiante(
            @PathVariable Long estudianteId) {
        Map<String, Object> stats = asistenciaService.obtenerEstadisticasEstudiante(estudianteId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/estadisticas/estudiante/{estudianteId}/rango")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasEstudianteEnRango(
            @PathVariable Long estudianteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        Map<String, Object> stats =
                asistenciaService.obtenerEstadisticasEstudianteEnRango(estudianteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(stats);
    }

    // ==========================================
    // REPORTES
    // ==========================================

    @GetMapping("/reporte/colegio/{colegioId}")
    public ResponseEntity<List<AsistenciaResponseDTO>> generarReportePorColegio(
            @PathVariable Long colegioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<AsistenciaResponseDTO> reporte =
                asistenciaService.generarReportePorColegio(colegioId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/reporte/estudiante/{estudianteId}")
    public ResponseEntity<List<AsistenciaResponseDTO>> generarReportePorEstudiante(
            @PathVariable Long estudianteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<AsistenciaResponseDTO> reporte =
                asistenciaService.generarReportePorEstudiante(estudianteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    @GetMapping("/existe")
    public ResponseEntity<Map<String, Boolean>> existeRegistro(
            @RequestParam Long estudianteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam TipoRecorrido tipoRecorrido) {
        boolean existe = asistenciaService.existeRegistro(estudianteId, fecha, tipoRecorrido);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    // ==========================================
    // REGISTRO MASIVO
    // ==========================================

    @PostMapping("/registrar-masivo")
    public ResponseEntity<List<AsistenciaResponseDTO>> registrarMasivo(
            @RequestBody List<RegistrarAsistenciaDTO> dtos,
            @RequestParam Long monitorId) {
        try {
            List<AsistenciaResponseDTO> asistencias = asistenciaService.registrarMasivo(dtos, monitorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(asistencias);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
