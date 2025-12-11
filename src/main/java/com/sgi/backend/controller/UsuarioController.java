package com.sgi.backend.controller;

import com.sgi.backend.dto.usuario.CrearUsuarioDTO;
import com.sgi.backend.dto.usuario.ActualizarUsuarioDTO;
import com.sgi.backend.dto.usuario.UsuarioResponseDTO;
import com.sgi.backend.model.Rol;
import com.sgi.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ==========================================
    // CREAR
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody CrearUsuarioDTO dto) {
        try {
            UsuarioResponseDTO usuario = usuarioService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // LISTAR
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivos() {
        return ResponseEntity.ok(usuarioService.listarActivos());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorRol(@PathVariable Rol rol) {
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/rol/{rol}/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivosPorRol(@PathVariable Rol rol) {
        return ResponseEntity.ok(usuarioService.listarActivosPorRol(rol));
    }

    // ==========================================
    // BUSCAR
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/documento/{numId}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorNumId(@PathVariable String numId) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorNumId(numId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre));
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioDTO dto) {
        try {
            return ResponseEntity.ok(usuarioService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // CAMBIAR CONTRASEÑA
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwords) {
        try {
            usuarioService.cambiarContrasena(
                    id,
                    passwords.get("contrasenaActual"),
                    passwords.get("contrasenaNueva")
            );
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // CAMBIO DE CONTRASEÑA PRIMER INGRESO
    // ==========================================
    @PostMapping("/{id}/cambiar-contrasena-primer-ingreso")
    public ResponseEntity<?> cambiarContrasenaPrimerIngreso(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String contrasenaNueva = body.get("contrasenaNueva");
            String confirmarContrasena = body.get("confirmarContrasena");

            if (!contrasenaNueva.equals(confirmarContrasena)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Las contraseñas no coinciden"));
            }

            usuarioService.cambiarContrasenaPrimerIngreso(id, contrasenaNueva);

            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // RESETEAR CONTRASEÑA (ADMIN)
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/{id}/resetear-contrasena")
    public ResponseEntity<?> resetearContrasena(@PathVariable Long id) {
        try {
            usuarioService.resetearContrasena(id);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Contraseña reseteada exitosamente",
                    "contrasenaGenerica", usuarioService.obtenerContrasenaGenerica()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // CONSULTAR CONTRASEÑA GENÉRICA (ADMIN)
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/contrasena-generica")
    public ResponseEntity<?> obtenerContrasenaGenerica() {
        return ResponseEntity.ok(Map.of(
                "contrasenaGenerica", usuarioService.obtenerContrasenaGenerica()
        ));
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<UsuarioResponseDTO> activar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.activar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDTO> desactivar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.desactivar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // ELIMINAR
    // ==========================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','ENCARGADO')")
    @GetMapping("/estadisticas/rol/{rol}")
    public ResponseEntity<Map<String, Long>> contarPorRol(@PathVariable Rol rol) {
        Long total = usuarioService.contarPorRol(rol);
        Long activos = usuarioService.contarActivosPorRol(rol);

        return ResponseEntity.ok(
                Map.of("total", total, "activos", activos)
        );
    }
}
