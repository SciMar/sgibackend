package com.sgi.backend.controller;

import com.sgi.backend.dto.colegiojornada.AsignarJornadaDTO;
import com.sgi.backend.dto.colegiojornada.ColegioJornadaResponseDTO;
import com.sgi.backend.service.ColegioJornadaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
@RestController
@RequestMapping("/api/colegio-jornadas")
@CrossOrigin(origins = "*")
public class ColegioJornadaController {

    @Autowired
    private ColegioJornadaService colegioJornadaService;

    // ==========================================
    // ASIGNAR
    // ==========================================

    @PostMapping
    public ResponseEntity<ColegioJornadaResponseDTO> asignar(@Valid @RequestBody AsignarJornadaDTO dto) {
        try {
            ColegioJornadaResponseDTO colegioJornada = colegioJornadaService.asignar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(colegioJornada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<ColegioJornadaResponseDTO>> listarTodas() {
        List<ColegioJornadaResponseDTO> relaciones = colegioJornadaService.listarTodas();
        return ResponseEntity.ok(relaciones);
    }

    @GetMapping("/colegio/{colegioId}")
    public ResponseEntity<List<ColegioJornadaResponseDTO>> listarJornadasDeColegio(@PathVariable Long colegioId) {
        List<ColegioJornadaResponseDTO> jornadas = colegioJornadaService.listarJornadasDeColegio(colegioId);
        return ResponseEntity.ok(jornadas);
    }

    @GetMapping("/colegio/{colegioId}/activas")
    public ResponseEntity<List<ColegioJornadaResponseDTO>> listarJornadasActivasDeColegio(@PathVariable Long colegioId) {
        List<ColegioJornadaResponseDTO> jornadas = colegioJornadaService.listarJornadasActivasDeColegio(colegioId);
        return ResponseEntity.ok(jornadas);
    }

    @GetMapping("/jornada/{jornadaId}")
    public ResponseEntity<List<ColegioJornadaResponseDTO>> listarColegiosDeJornada(@PathVariable Long jornadaId) {
        List<ColegioJornadaResponseDTO> colegios = colegioJornadaService.listarColegiosDeJornada(jornadaId);
        return ResponseEntity.ok(colegios);
    }

    @GetMapping("/jornada/{jornadaId}/activos")
    public ResponseEntity<List<ColegioJornadaResponseDTO>> listarColegiosActivosDeJornada(@PathVariable Long jornadaId) {
        List<ColegioJornadaResponseDTO> colegios = colegioJornadaService.listarColegiosActivosDeJornada(jornadaId);
        return ResponseEntity.ok(colegios);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<ColegioJornadaResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            ColegioJornadaResponseDTO colegioJornada = colegioJornadaService.obtenerPorId(id);
            return ResponseEntity.ok(colegioJornada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/colegio/{colegioId}/jornada/{jornadaId}")
    public ResponseEntity<ColegioJornadaResponseDTO> obtenerPorColegioYJornada(
            @PathVariable Long colegioId,
            @PathVariable Long jornadaId) {
        try {
            ColegioJornadaResponseDTO colegioJornada =
                    colegioJornadaService.obtenerPorColegioYJornada(colegioId, jornadaId);
            return ResponseEntity.ok(colegioJornada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ColegioJornadaResponseDTO> activar(@PathVariable Long id) {
        try {
            ColegioJornadaResponseDTO colegioJornada = colegioJornadaService.activar(id);
            return ResponseEntity.ok(colegioJornada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ColegioJornadaResponseDTO> desactivar(@PathVariable Long id) {
        try {
            ColegioJornadaResponseDTO colegioJornada = colegioJornadaService.desactivar(id);
            return ResponseEntity.ok(colegioJornada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/colegio/{colegioId}/jornada/{jornadaId}/activar")
    public ResponseEntity<Void> activarPorColegioYJornada(
            @PathVariable Long colegioId,
            @PathVariable Long jornadaId) {
        try {
            colegioJornadaService.activarPorColegioYJornada(colegioId, jornadaId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/colegio/{colegioId}/jornada/{jornadaId}/desactivar")
    public ResponseEntity<Void> desactivarPorColegioYJornada(
            @PathVariable Long colegioId,
            @PathVariable Long jornadaId) {
        try {
            colegioJornadaService.desactivarPorColegioYJornada(colegioId, jornadaId);
            return ResponseEntity.ok().build();
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
            colegioJornadaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/colegio/{colegioId}/jornada/{jornadaId}")
    public ResponseEntity<Void> eliminarPorColegioYJornada(
            @PathVariable Long colegioId,
            @PathVariable Long jornadaId) {
        try {
            colegioJornadaService.eliminarPorColegioYJornada(colegioId, jornadaId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // OPERACIONES MASIVAS
    // ==========================================

    @PostMapping("/colegio/{colegioId}/asignar-varias")
    public ResponseEntity<List<ColegioJornadaResponseDTO>> asignarVariasJornadas(
            @PathVariable Long colegioId,
            @RequestBody List<Long> jornadaIds) {
        try {
            List<ColegioJornadaResponseDTO> asignaciones =
                    colegioJornadaService.asignarVariasJornadas(colegioId, jornadaIds);
            return ResponseEntity.ok(asignaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
