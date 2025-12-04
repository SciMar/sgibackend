package com.sgi.backend.service;

import com.sgi.backend.dto.asistencia.RegistrarAsistenciaDTO;
import com.sgi.backend.dto.asistencia.AsistenciaResponseDTO;
import com.sgi.backend.model.*;
import com.sgi.backend.repository.AsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private EstudianteService estudianteService;

    @Mock
    private MonitorService monitorService;

    @InjectMocks
    private AsistenciaService asistenciaService;

    private Estudiante estudianteMock;
    private Monitor monitorMock;
    private Zona zonaMock;
    private Jornada jornadaMock;
    private Colegio colegioMock;

    @BeforeEach
    void setUp() {
        // Zona
        zonaMock = new Zona();
        zonaMock.setId(1L);
        zonaMock.setCodigoZona("ZONA1");
        zonaMock.setNombreZona("Usaquén");

        // Jornada
        jornadaMock = new Jornada();
        jornadaMock.setId(1L);
        jornadaMock.setCodigoJornada("J001");
        jornadaMock.setNombreJornada(TipoJornada.MANANA);
        jornadaMock.setZona(zonaMock);

        // Colegio
        colegioMock = new Colegio();
        colegioMock.setId(1L);
        colegioMock.setNombreColegio("Colegio Test");
        colegioMock.setZona(zonaMock);

        // Estudiante
        estudianteMock = new Estudiante();
        estudianteMock.setId(1L);
        estudianteMock.setTipoId(TipoId.TI);
        estudianteMock.setNumId("1234567890");
        estudianteMock.setPrimerNombre("Juan");
        estudianteMock.setPrimerApellido("Pérez");
        estudianteMock.setColegio(colegioMock);
        estudianteMock.setJornada(jornadaMock);
        estudianteMock.setActivo(true);

        // Usuario del monitor
        Usuario usuarioMonitor = new Usuario();
        usuarioMonitor.setId(1L);
        usuarioMonitor.setEmail("monitor@ciempies.com");
        usuarioMonitor.setPrimerNombre("Carlos");
        usuarioMonitor.setPrimerApellido("Monitor");

        // Monitor
        monitorMock = new Monitor();
        monitorMock.setId(1L);
        monitorMock.setUsuario(usuarioMonitor);
        monitorMock.setZona(zonaMock);
        monitorMock.setJornada(jornadaMock);
        monitorMock.setActivo(true);
    }

    @Test
    void testRegistrarAsistenciaExitoso() {
        // Arrange
        RegistrarAsistenciaDTO dto = new RegistrarAsistenciaDTO();
        dto.setEstudianteId(1L);
        dto.setTipoRecorrido(TipoRecorrido.IDA);
        dto.setEstadoAsistencia(EstadoAsistencia.PRESENTE);
        dto.setObservaciones("Ninguna");

        when(estudianteService.obtenerEntidadPorId(1L)).thenReturn(estudianteMock);
        when(monitorService.obtenerEntidadPorId(1L)).thenReturn(monitorMock);
        when(asistenciaRepository.existsByEstudianteIdAndFechaAndTipoRecorrido(any(), any(), any()))
                .thenReturn(false);
        when(asistenciaRepository.save(any(Asistencia.class)))
                .thenAnswer(invocation -> {
                    Asistencia asistencia = invocation.getArgument(0);
                    asistencia.setId(1L);
                    return asistencia;
                });

        // Act
        AsistenciaResponseDTO resultado = asistenciaService.registrar(dto, 1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getEstudianteId());
        assertEquals(TipoRecorrido.IDA, resultado.getTipoRecorrido());
        assertEquals(EstadoAsistencia.PRESENTE, resultado.getEstadoAsistencia());
        verify(asistenciaRepository, times(1)).save(any(Asistencia.class));
    }

    @Test
    void testRegistrarAsistenciaDuplicada() {
        // Arrange
        RegistrarAsistenciaDTO dto = new RegistrarAsistenciaDTO();
        dto.setEstudianteId(1L);
        dto.setTipoRecorrido(TipoRecorrido.IDA);
        dto.setEstadoAsistencia(EstadoAsistencia.PRESENTE);

        when(estudianteService.obtenerEntidadPorId(1L)).thenReturn(estudianteMock);
        when(monitorService.obtenerEntidadPorId(1L)).thenReturn(monitorMock);
        when(asistenciaRepository.existsByEstudianteIdAndFechaAndTipoRecorrido(
                1L, LocalDate.now(), TipoRecorrido.IDA))
                .thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            asistenciaService.registrar(dto, 1L);
        });

        assertTrue(exception.getMessage().contains("Ya existe un registro de asistencia"));
        verify(asistenciaRepository, never()).save(any());
    }

    @Test
    void testObtenerEstadisticasDeHoy() {
        // Arrange
        when(asistenciaRepository.countByFecha(any())).thenReturn(50L);
        when(asistenciaRepository.contarPresentesDeHoy()).thenReturn(45L);
        when(asistenciaRepository.contarAusentesDeHoy()).thenReturn(5L);

        // Act
        Map<String, Long> estadisticas = asistenciaService.obtenerEstadisticasDeHoy();

        // Assert
        assertNotNull(estadisticas);
        assertEquals(50L, estadisticas.get("total"));
        assertEquals(45L, estadisticas.get("presentes"));
        assertEquals(5L, estadisticas.get("ausentes"));
    }
}
