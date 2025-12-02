package com.sgi.backend.service;

import com.sgi.backend.dto.monitor.CrearMonitorDTO;
import com.sgi.backend.dto.monitor.ActualizarMonitorDTO;
import com.sgi.backend.dto.monitor.MonitorResponseDTO;
import com.sgi.backend.model.Monitor;
import com.sgi.backend.model.Usuario;
import com.sgi.backend.model.Zona;
import com.sgi.backend.model.Jornada;
import com.sgi.backend.model.Rol;
import com.sgi.backend.repository.MonitorRepository;
import com.sgi.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MonitorService {

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ZonaService zonaService;

    @Autowired
    private JornadaService jornadaService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==========================================
    // CREAR (Crea usuario + monitor)
    // ==========================================

    public MonitorResponseDTO crear(CrearMonitorDTO dto) {
        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }

        // Validar que no exista el número de identificación
        if (usuarioRepository.existsByNumId(dto.getNumId())) {
            throw new RuntimeException("Ya existe un usuario con el número de identificación: " + dto.getNumId());
        }

        // Obtener zona y jornada
        Zona zona = zonaService.obtenerEntidadPorId(dto.getZonaId());
        Jornada jornada = jornadaService.obtenerEntidadPorId(dto.getJornadaId());

        // Validar que la jornada pertenezca a la zona
        if (!jornada.getZona().getId().equals(zona.getId())) {
            throw new RuntimeException("La jornada seleccionada no pertenece a la zona seleccionada");
        }

        // 1. Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setTipoId(dto.getTipoId());
        usuario.setNumId(dto.getNumId());
        usuario.setPrimerNombre(dto.getPrimerNombre());
        usuario.setSegundoNombre(dto.getSegundoNombre());
        usuario.setPrimerApellido(dto.getPrimerApellido());
        usuario.setSegundoApellido(dto.getSegundoApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setRol(Rol.MONITOR);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 2. Crear el monitor
        Monitor monitor = new Monitor();
        monitor.setUsuario(usuarioGuardado);
        monitor.setZona(zona);
        monitor.setJornada(jornada);
        monitor.setFechaAsignacion(LocalDate.now());
        monitor.setActivo(true);

        Monitor monitorGuardado = monitorRepository.save(monitor);

        return convertirAResponseDTO(monitorGuardado);
    }

    // ==========================================
    // ACTUALIZAR (Solo actualiza zona y jornada)
    // ==========================================

    public MonitorResponseDTO actualizar(Long id, ActualizarMonitorDTO dto) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monitor no encontrado con id: " + id));

        // Obtener zona y jornada
        Zona zona = zonaService.obtenerEntidadPorId(dto.getZonaId());
        Jornada jornada = jornadaService.obtenerEntidadPorId(dto.getJornadaId());

        // Validar que la jornada pertenezca a la zona
        if (!jornada.getZona().getId().equals(zona.getId())) {
            throw new RuntimeException("La jornada seleccionada no pertenece a la zona seleccionada");
        }

        monitor.setZona(zona);
        monitor.setJornada(jornada);

        Monitor monitorActualizado = monitorRepository.save(monitor);
        return convertirAResponseDTO(monitorActualizado);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<MonitorResponseDTO> listarTodos() {
        return monitorRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MonitorResponseDTO> listarActivos() {
        return monitorRepository.findByActivoTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MonitorResponseDTO> listarPorZona(Long zonaId) {
        return monitorRepository.findByZonaId(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MonitorResponseDTO> listarActivosPorZona(Long zonaId) {
        return monitorRepository.findByZonaIdAndActivoTrue(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MonitorResponseDTO> listarPorZonaYJornada(Long zonaId, Long jornadaId) {
        return monitorRepository.findByZonaIdAndJornadaId(zonaId, jornadaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public MonitorResponseDTO obtenerPorId(Long id) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monitor no encontrado con id: " + id));
        return convertirAResponseDTO(monitor);
    }

    public MonitorResponseDTO obtenerPorUsuarioId(Long usuarioId) {
        Monitor monitor = monitorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró monitor para el usuario con id: " + usuarioId));
        return convertirAResponseDTO(monitor);
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    public MonitorResponseDTO activar(Long id) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monitor no encontrado con id: " + id));

        // También activar el usuario asociado
        Usuario usuario = monitor.getUsuario();
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        monitor.setActivo(true);
        Monitor monitorActualizado = monitorRepository.save(monitor);
        return convertirAResponseDTO(monitorActualizado);
    }

    public MonitorResponseDTO desactivar(Long id) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monitor no encontrado con id: " + id));

        // También desactivar el usuario asociado
        Usuario usuario = monitor.getUsuario();
        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        monitor.setActivo(false);
        Monitor monitorActualizado = monitorRepository.save(monitor);
        return convertirAResponseDTO(monitorActualizado);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monitor no encontrado con id: " + id));

        // Verificar si ha registrado asistencias
        Long totalAsistencias = monitorRepository.contarAsistenciasPorMonitor(id);
        if (totalAsistencias > 0) {
            throw new RuntimeException("No se puede eliminar el monitor porque ha registrado " +
                    totalAsistencias + " asistencias. Considere desactivarlo en su lugar.");
        }

        Usuario usuario = monitor.getUsuario();

        // Eliminar monitor
        monitorRepository.deleteById(id);

        // Eliminar usuario asociado
        usuarioRepository.deleteById(usuario.getId());
    }

    // ==========================================
    // REASIGNAR
    // ==========================================

    public MonitorResponseDTO reasignar(Long monitorId, Long zonaId, Long jornadaId) {
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new RuntimeException("Monitor no encontrado con id: " + monitorId));

        Zona zona = zonaService.obtenerEntidadPorId(zonaId);
        Jornada jornada = jornadaService.obtenerEntidadPorId(jornadaId);

        // Validar que la jornada pertenezca a la zona
        if (!jornada.getZona().getId().equals(zona.getId())) {
            throw new RuntimeException("La jornada seleccionada no pertenece a la zona seleccionada");
        }

        monitor.setZona(zona);
        monitor.setJornada(jornada);
        monitor.setFechaAsignacion(LocalDate.now()); // Nueva fecha de asignación

        Monitor monitorReasignado = monitorRepository.save(monitor);
        return convertirAResponseDTO(monitorReasignado);
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    public MonitorResponseDTO obtenerConEstadisticas(Long id) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monitor no encontrado con id: " + id));

        MonitorResponseDTO response = convertirAResponseDTO(monitor);
        response.setTotalAsistenciasRegistradas(monitorRepository.contarAsistenciasPorMonitor(id));

        return response;
    }

    public Long contarAsistenciasRegistradas(Long monitorId) {
        return monitorRepository.contarAsistenciasPorMonitor(monitorId);
    }

    // ==========================================
    // BÚSQUEDAS POR FECHA
    // ==========================================

    public List<MonitorResponseDTO> listarAsignadosRecientes(int dias) {
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        return monitorRepository.findMonitoresAsignadosRecientes(fechaLimite).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MonitorResponseDTO> listarAsignadosEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return monitorRepository.findByFechaAsignacionBetween(fechaInicio, fechaFin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private MonitorResponseDTO convertirAResponseDTO(Monitor monitor) {
        MonitorResponseDTO dto = new MonitorResponseDTO();
        dto.setId(monitor.getId());
        dto.setFechaAsignacion(monitor.getFechaAsignacion());
        dto.setActivo(monitor.getActivo());

        // Datos del usuario
        if (monitor.getUsuario() != null) {
            Usuario usuario = monitor.getUsuario();
            dto.setUsuarioId(usuario.getId());
            dto.setNombreCompleto(usuario.getPrimerNombre() + " " +
                    (usuario.getSegundoNombre() != null ? usuario.getSegundoNombre() + " " : "") +
                    usuario.getPrimerApellido() + " " +
                    (usuario.getSegundoApellido() != null ? usuario.getSegundoApellido() : ""));
            dto.setEmail(usuario.getEmail());
        }

        // Datos de zona
        if (monitor.getZona() != null) {
            dto.setZonaId(monitor.getZona().getId());
            dto.setNombreZona(monitor.getZona().getNombreZona());
        }

        // Datos de jornada
        if (monitor.getJornada() != null) {
            dto.setJornadaId(monitor.getJornada().getId());
            dto.setNombreJornada(monitor.getJornada().getNombreJornada().toString());
        }

        return dto;
    }

    // Método interno para obtener entidad (usado por otros services)
    public Monitor obtenerEntidadPorId(Long id) {
        return monitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Monitor no encontrado con id: " + id));
    }

    public Monitor obtenerEntidadPorUsuarioId(Long usuarioId) {
        return monitorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró monitor para el usuario con id: " + usuarioId));
    }
}
