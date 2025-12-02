package com.sgi.backend.controller;

import com.sgi.backend.dto.auth.LoginResponseDTO;
import com.sgi.backend.dto.usuario.LoginDTO;
import com.sgi.backend.dto.usuario.UsuarioResponseDTO;
import com.sgi.backend.security.JwtUtil;
import com.sgi.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            // Validar credenciales
            UsuarioResponseDTO usuario = usuarioService.login(loginDTO);

            // Generar token JWT
            String token = jwtUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getRol().name(),
                    usuario.getId()
            );

            // Crear respuesta con usuario y token
            LoginResponseDTO response = new LoginResponseDTO(usuario, token, "Bearer");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
