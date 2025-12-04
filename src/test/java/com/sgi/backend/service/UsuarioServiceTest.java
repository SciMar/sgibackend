package com.sgi.backend.service;

import com.sgi.backend.dto.usuario.LoginDTO;
import com.sgi.backend.dto.usuario.UsuarioResponseDTO;
import com.sgi.backend.model.Rol;
import com.sgi.backend.model.TipoId;
import com.sgi.backend.model.Usuario;
import com.sgi.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setTipoId(TipoId.CC);
        usuarioMock.setNumId("1000000001");
        usuarioMock.setPrimerNombre("Juan");
        usuarioMock.setPrimerApellido("Pérez");
        usuarioMock.setEmail("juan@ciempies.com");
        usuarioMock.setContrasena("$2a$10$UB5trHg7K/IWB3.Rg5tnyeagnQUrGgdD8epXkCLbqtQpMvGz4DKbG");
        usuarioMock.setRol(Rol.ADMINISTRADOR);
        usuarioMock.setActivo(true);
    }

    @Test
    void testLoginExitoso() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("juan@ciempies.com");
        loginDTO.setContrasena("admin123");

        when(usuarioRepository.findByEmail("juan@ciempies.com"))
                .thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        // Act
        UsuarioResponseDTO resultado = usuarioService.login(loginDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("juan@ciempies.com", resultado.getEmail());
        assertEquals(Rol.ADMINISTRADOR, resultado.getRol());
        verify(usuarioRepository, times(1)).findByEmail("juan@ciempies.com");
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    void testLoginUsuarioNoExiste() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("noexiste@ciempies.com");
        loginDTO.setContrasena("password123");

        when(usuarioRepository.findByEmail("noexiste@ciempies.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.login(loginDTO);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail("noexiste@ciempies.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void testLoginUsuarioInactivo() {
        // Arrange
        usuarioMock.setActivo(false);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("juan@ciempies.com");
        loginDTO.setContrasena("admin123");

        when(usuarioRepository.findByEmail("juan@ciempies.com"))
                .thenReturn(Optional.of(usuarioMock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.login(loginDTO);
        });

        assertEquals("Usuario inactivo. Contacte al administrador.", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void testLoginContrasenaIncorrecta() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("juan@ciempies.com");
        loginDTO.setContrasena("passwordIncorrecta");

        when(usuarioRepository.findByEmail("juan@ciempies.com"))
                .thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.login(loginDTO);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    void testObtenerPorIdExitoso() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // Act
        UsuarioResponseDTO resultado = usuarioService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("juan@ciempies.com", resultado.getEmail());
        assertEquals(Rol.ADMINISTRADOR, resultado.getRol());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorIdNoExiste() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.obtenerPorId(999L);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    void testListarTodos() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setTipoId(TipoId.CC);
        usuario2.setNumId("1000000002");
        usuario2.setPrimerNombre("María");
        usuario2.setPrimerApellido("López");
        usuario2.setEmail("maria@ciempies.com");
        usuario2.setRol(Rol.ENCARGADO);
        usuario2.setActivo(true);

        List<Usuario> usuarios = Arrays.asList(usuarioMock, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("juan@ciempies.com", resultado.get(0).getEmail());
        assertEquals("maria@ciempies.com", resultado.get(1).getEmail());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testListarPorRol() {
        // Arrange
        when(usuarioRepository.findByRol(Rol.ADMINISTRADOR))
                .thenReturn(Arrays.asList(usuarioMock));

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.listarPorRol(Rol.ADMINISTRADOR);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(Rol.ADMINISTRADOR, resultado.get(0).getRol());
        verify(usuarioRepository, times(1)).findByRol(Rol.ADMINISTRADOR);
    }

    @Test
    void testObtenerPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmail("juan@ciempies.com"))
                .thenReturn(Optional.of(usuarioMock));

        // Act
        UsuarioResponseDTO resultado = usuarioService.obtenerPorEmail("juan@ciempies.com");

        // Assert
        assertNotNull(resultado);
        assertEquals("juan@ciempies.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).findByEmail("juan@ciempies.com");
    }
}
