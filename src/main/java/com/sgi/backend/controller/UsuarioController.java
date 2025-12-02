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

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarActivos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorRol(@PathVariable Rol rol) {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/rol/{rol}/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivosPorRol(@PathVariable Rol rol) {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarActivosPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        try {
            UsuarioResponseDTO usuario = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorEmail(@PathVariable String email) {
        try {
            UsuarioResponseDTO usuario = usuarioService.obtenerPorEmail(email);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/documento/{numId}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorNumId(@PathVariable String numId) {
        try {
            UsuarioResponseDTO usuario = usuarioService.obtenerPorNumId(numId);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorNombre(nombre);
        return ResponseEntity.ok(usuarios);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioDTO dto) {
        try {
            UsuarioResponseDTO usuario = usuarioService.actualizar(id, dto);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // CAMBIAR CONTRASEÑA
    // ==========================================

    @PostMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwords) {
        try {
            String contrasenaActual = passwords.get("contrasenaActual");
            String contrasenaNueva = passwords.get("contrasenaNueva");
            usuarioService.cambiarContrasena(id, contrasenaActual, contrasenaNueva);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    @PatchMapping("/{id}/activar")
    public ResponseEntity<UsuarioResponseDTO> activar(@PathVariable Long id) {
        try {
            UsuarioResponseDTO usuario = usuarioService.activar(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDTO> desactivar(@PathVariable Long id) {
        try {
            UsuarioResponseDTO usuario = usuarioService.desactivar(id);
            return ResponseEntity.ok(usuario);
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
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    @GetMapping("/estadisticas/rol/{rol}")
    public ResponseEntity<Map<String, Long>> contarPorRol(@PathVariable Rol rol) {
        Long total = usuarioService.contarPorRol(rol);
        Long activos = usuarioService.contarActivosPorRol(rol);
        return ResponseEntity.ok(Map.of(
                "total", total,
                "activos", activos
        ));
    }
}