package com.sgi.backend.service;

import com.sgi.backend.dto.usuario.CrearUsuarioDTO;
import com.sgi.backend.dto.usuario.ActualizarUsuarioDTO;
import com.sgi.backend.dto.usuario.LoginDTO;
import com.sgi.backend.dto.usuario.UsuarioResponseDTO;
import com.sgi.backend.model.Usuario;
import com.sgi.backend.model.Rol;
import com.sgi.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==========================================
    // CREAR
    // ==========================================

    public UsuarioResponseDTO crear(CrearUsuarioDTO dto) {
        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        // Validar que no exista el número de identificación
        if (usuarioRepository.existsByNumId(dto.getNumId())) {
            throw new RuntimeException("Ya existe un usuario con el número de identificación: " + dto.getNumId());
        }

        Usuario usuario = new Usuario();
        usuario.setTipoId(dto.getTipoId());
        usuario.setNumId(dto.getNumId());
        usuario.setPrimerNombre(dto.getPrimerNombre());
        usuario.setSegundoNombre(dto.getSegundoNombre());
        usuario.setPrimerApellido(dto.getPrimerApellido());
        usuario.setSegundoApellido(dto.getSegundoApellido());
        usuario.setEmail(dto.getEmail());

        // Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        usuario.setRol(dto.getRol());
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuarioGuardado);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    public UsuarioResponseDTO actualizar(Long id, ActualizarUsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        // Validar email si cambió
        if (!usuario.getEmail().equals(dto.getEmail())) {
            if (usuarioRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw new RuntimeException("Ya existe otro usuario con ese email");
            }
            usuario.setEmail(dto.getEmail());
        }

        // Validar numId si cambió
        if (!usuario.getNumId().equals(dto.getNumId())) {
            if (usuarioRepository.existsByNumIdAndIdNot(dto.getNumId(), id)) {
                throw new RuntimeException("Ya existe otro usuario con ese número de identificación");
            }
            usuario.setNumId(dto.getNumId());
        }

        usuario.setTipoId(dto.getTipoId());
        usuario.setPrimerNombre(dto.getPrimerNombre());
        usuario.setSegundoNombre(dto.getSegundoNombre());
        usuario.setPrimerApellido(dto.getPrimerApellido());
        usuario.setSegundoApellido(dto.getSegundoApellido());

        // Actualizar contraseña solo si se proporciona una nueva
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuarioActualizado);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> listarActivos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> listarActivosPorRol(Rol rol) {
        return usuarioRepository.findByRolAndActivoTrue(rol).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return convertirAResponseDTO(usuario);
    }

    public UsuarioResponseDTO obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return convertirAResponseDTO(usuario);
    }

    public UsuarioResponseDTO obtenerPorNumId(String numId) {
        Usuario usuario = usuarioRepository.findByNumId(numId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con número de identificación: " + numId));
        return convertirAResponseDTO(usuario);
    }

    public List<UsuarioResponseDTO> buscarPorNombre(String nombre) {
        return usuarioRepository.buscarPorNombreOApellido(nombre).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    public UsuarioResponseDTO activar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        usuario.setActivo(true);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuarioActualizado);
    }

    public UsuarioResponseDTO desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        usuario.setActivo(false);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuarioActualizado);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        // Verificar si es un monitor (tiene relación con tabla monitores)
        if (usuario.getRol() == Rol.MONITOR && usuario.getMonitor() != null) {
            throw new RuntimeException("No se puede eliminar el usuario porque está asignado como monitor. " +
                    "Primero elimine la asignación de monitor.");
        }

        usuarioRepository.deleteById(id);
    }

    // ==========================================
    // AUTENTICACIÓN
    // ==========================================

    public UsuarioResponseDTO login(LoginDTO dto) {
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador.");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return convertirAResponseDTO(usuario);
    }

    // Cambiar contraseña
    public void cambiarContrasena(Long id, String contrasenaActual, String contrasenaNueva) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Validar que la nueva contraseña sea diferente
        if (passwordEncoder.matches(contrasenaNueva, usuario.getContrasena())) {
            throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
        }

        // Actualizar contraseña
        usuario.setContrasena(passwordEncoder.encode(contrasenaNueva));
        usuarioRepository.save(usuario);
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    public Long contarPorRol(Rol rol) {
        return usuarioRepository.countByRol(rol);
    }

    public Long contarActivosPorRol(Rol rol) {
        return usuarioRepository.countByRolAndActivoTrue(rol);
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private UsuarioResponseDTO convertirAResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setTipoId(usuario.getTipoId());
        dto.setNumId(usuario.getNumId());
        dto.setPrimerNombre(usuario.getPrimerNombre());
        dto.setSegundoNombre(usuario.getSegundoNombre());
        dto.setPrimerApellido(usuario.getPrimerApellido());
        dto.setSegundoApellido(usuario.getSegundoApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        // NO incluir contraseña en la respuesta
        return dto;
    }

    // Método interno para obtener entidad (usado por otros services)
    public Usuario obtenerEntidadPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }
}
