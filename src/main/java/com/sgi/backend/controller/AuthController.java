package com.sgi.backend.controller;

import com.sgi.backend.dto.auth.LoginResponseDTO;
import com.sgi.backend.dto.usuario.LoginDTO;
import com.sgi.backend.dto.usuario.UsuarioResponseDTO;
import com.sgi.backend.security.JwtUtil;
import com.sgi.backend.service.UsuarioService;
import com.sgi.backend.model.Usuario;
import com.sgi.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map; // ← AGREGAR ESTO

@Tag(name = "0-Auth", description = "Endpoints de autenticación")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        System.out.println("=== LOGIN REQUEST RECIBIDO ===");
        System.out.println("Email recibido: " + loginDTO.getEmail());

        try {
            UsuarioResponseDTO usuario = usuarioService.login(loginDTO);
            System.out.println("✓ Usuario autenticado: " + usuario.getEmail());

            String token = jwtUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getRol().name(),
                    usuario.getId()
            );

            System.out.println("✓ Token generado");

            LoginResponseDTO response = new LoginResponseDTO(usuario, token, "Bearer");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.out.println("✗ ERROR EN LOGIN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/fix-password")
    public ResponseEntity<?> fixPassword() {
        // ✅ CORREGIDO - usa minúscula
        Usuario admin = usuarioRepository.findByEmail("admin@ciempies.com")
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nuevoHash = passwordEncoder.encode("admin123");
        admin.setContrasena(nuevoHash);
        usuarioRepository.save(admin);

        System.out.println("✓ Contraseña actualizada");
        System.out.println("Nuevo hash: " + nuevoHash);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Contraseña actualizada correctamente",
                "nuevoHash", nuevoHash
        ));
    }
}
