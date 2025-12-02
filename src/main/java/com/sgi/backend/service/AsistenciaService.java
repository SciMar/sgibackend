package com.sgi.backend.service;

import com.sgi.backend.dto.asistencia.RegistrarAsistenciaDTO;
import com.sgi.backend.dto.asistencia.ActualizarAsistenciaDTO;
import com.sgi.backend.dto.asistencia.AsistenciaResponseDTO;
import com.sgi.backend.model.Asistencia;
import com.sgi.backend.model.Estudiante;
import com.sgi.backend.model.Monitor;
import com.sgi.backend.model.EstadoAsistencia;
import com.sgi.backend.model.TipoRecorrido;
import com.sgi.backend.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private MonitorService monitorService;

    // ==========================================
    // REGISTRAR ASISTENCIA
    // ==========================================

    public AsistenciaResponseDTO registrar(RegistrarAsistenciaDTO dto, Long monitorId) {
        // Obtener estudiante
        Estudiante estudiante = estudianteService.obtenerEntidadPorId(dto.getEstudianteId());

        // Obtener monitor
        Monitor monitor = monitorService.obtenerEntidadPorId(monitorId);

        // Validar que el monitor puede registrar asistencia de este estudiante
        validarPermisoMonitor(monitor, estudiante);

        // Determinar fecha
        LocalDate fecha = dto.getFecha() != null ? dto.getFecha() : LocalDate.now();

        // Validar que no exista ya una asistencia para este estudiante, fecha y tipo de recorrido
        if (asistenciaRepository.existsByEstudianteIdAndFechaAndTipoRecorrido(
                dto.getEstudianteId(),
                fecha,
                dto.getTipoRecorrido())) {
            throw new RuntimeException("Ya existe un registro de asistencia " + dto.getTipoRecorrido() +
                    " para este estudiante en la fecha " + fecha);
        }

        Asistencia asistencia = new Asistencia();
        asistencia.setEstudiante(estudiante);
        asistencia.setColegio(estudiante.getColegio());
        asistencia.setFecha(fecha);
        asistencia.setHoraRegistro(LocalTime.now());
        asistencia.setTipoRecorrido(dto.getTipoRecorrido());
        asistencia.setEstadoAsistencia(dto.getEstadoAsistencia());
        asistencia.setObservaciones(dto.getObservaciones());
        asistencia.setMonitor(monitor);

        Asistencia asistenciaGuardada = asistenciaRepository.save(asistencia);
        return convertirAResponseDTO(asistenciaGuardada);
    }

    // Validar que el monitor tiene permiso para registrar asistencia de este estudiante
    private void validarPermisoMonitor(Monitor monitor, Estudiante estudiante) {
        // El estudiante debe estar en la misma zona y jornada del monitor
        if (!estudiante.getColegio().getZona().getId().equals(monitor.getZona().getId())) {
            throw new RuntimeException("El estudiante no pertenece a la zona asignada al monitor");
        }

        if (!estudiante.getJornada().getId().equals(monitor.getJornada().getId())) {
            throw new RuntimeException("El estudiante no pertenece a la jornada asignada al monitor");
        }

        // El estudiante debe estar activo
        if (!estudiante.getActivo()) {
            throw new RuntimeException("No se puede registrar asistencia de un estudiante inactivo");
        }
    }

    // ==========================================
    // ACTUALIZAR ASISTENCIA
    // ==========================================

    public AsistenciaResponseDTO actualizar(Long id, ActualizarAsistenciaDTO dto, Long monitorId) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));

        // Validar que el monitor que actualiza sea el mismo que registró
        if (!asistencia.getMonitor().getId().equals(monitorId)) {
            throw new RuntimeException("Solo el monitor que registró la asistencia puede actualizarla");
        }

        // Solo se puede actualizar el mismo día
        if (!asistencia.getFecha().equals(LocalDate.now())) {
            throw new RuntimeException("Solo se puede actualizar la asistencia el mismo día que fue registrada");
        }

        asistencia.setEstadoAsistencia(dto.getEstadoAsistencia());
        asistencia.setObservaciones(dto.getObservaciones());

        Asistencia asistenciaActualizada = asistenciaRepository.save(asistencia);
        return convertirAResponseDTO(asistenciaActualizada);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<AsistenciaResponseDTO> listarTodas() {
        return asistenciaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarDeHoy() {
        return asistenciaRepository.findAsistenciasDeHoy().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarPorFecha(LocalDate fecha) {
        return asistenciaRepository.findByFecha(fecha).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return asistenciaRepository.findByFechaBetween(fechaInicio, fechaFin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // LISTAR POR ESTUDIANTE
    // ==========================================

    public List<AsistenciaResponseDTO> listarPorEstudiante(Long estudianteId) {
        return asistenciaRepository.findByEstudianteId(estudianteId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarPorEstudianteYFecha(Long estudianteId, LocalDate fecha) {
        return asistenciaRepository.findByEstudianteIdAndFecha(estudianteId, fecha).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarPorEstudianteEnRango(Long estudianteId,
                                                                  LocalDate fechaInicio,
                                                                  LocalDate fechaFin) {
        return asistenciaRepository.findByEstudianteIdAndFechaBetween(estudianteId, fechaInicio, fechaFin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // LISTAR POR COLEGIO
    // ==========================================

    public List<AsistenciaResponseDTO> listarPorColegio(Long colegioId, LocalDate fecha) {
        return asistenciaRepository.findByColegioIdAndFecha(colegioId, fecha).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarPorColegioEnRango(Long colegioId,
                                                               LocalDate fechaInicio,
                                                               LocalDate fechaFin) {
        return asistenciaRepository.findByColegioIdAndFechaBetween(colegioId, fechaInicio, fechaFin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // LISTAR POR MONITOR
    // ==========================================

    public List<AsistenciaResponseDTO> listarPorMonitor(Long monitorId) {
        return asistenciaRepository.findByMonitorId(monitorId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarPorMonitorYFecha(Long monitorId, LocalDate fecha) {
        return asistenciaRepository.findByMonitorIdAndFecha(monitorId, fecha).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarPorMonitorEnRango(Long monitorId,
                                                               LocalDate fechaInicio,
                                                               LocalDate fechaFin) {
        return asistenciaRepository.findByMonitorIdAndFechaBetween(monitorId, fechaInicio, fechaFin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // LISTAR POR ZONA
    // ==========================================

    public List<AsistenciaResponseDTO> listarPorZona(Long zonaId, LocalDate fecha) {
        return asistenciaRepository.findByZonaIdAndFecha(zonaId, fecha).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // FILTROS POR TIPO Y ESTADO
    // ==========================================

    public List<AsistenciaResponseDTO> listarPorTipoRecorrido(TipoRecorrido tipoRecorrido, LocalDate fecha) {
        return asistenciaRepository.findByTipoRecorridoAndFecha(tipoRecorrido, fecha).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> listarPorEstadoAsistencia(EstadoAsistencia estadoAsistencia, LocalDate fecha) {
        return asistenciaRepository.findByEstadoAsistenciaAndFecha(estadoAsistencia, fecha).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public AsistenciaResponseDTO obtenerPorId(Long id) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));
        return convertirAResponseDTO(asistencia);
    }

    public AsistenciaResponseDTO obtenerPorEstudianteFechaYTipo(Long estudianteId,
                                                                LocalDate fecha,
                                                                TipoRecorrido tipoRecorrido) {
        Asistencia asistencia = asistenciaRepository.findByEstudianteIdAndFechaAndTipoRecorrido(
                        estudianteId, fecha, tipoRecorrido)
                .orElseThrow(() -> new RuntimeException("No se encontró asistencia para los parámetros especificados"));
        return convertirAResponseDTO(asistencia);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id, Long monitorId) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));

        // Validar que el monitor que elimina sea el mismo que registró
        if (!asistencia.getMonitor().getId().equals(monitorId)) {
            throw new RuntimeException("Solo el monitor que registró la asistencia puede eliminarla");
        }

        // Solo se puede eliminar el mismo día
        if (!asistencia.getFecha().equals(LocalDate.now())) {
            throw new RuntimeException("Solo se puede eliminar la asistencia el mismo día que fue registrada");
        }

        asistenciaRepository.deleteById(id);
    }

    // ==========================================
    // ESTADÍSTICAS GENERALES
    // ==========================================

    public Map<String, Long> obtenerEstadisticasDeHoy() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", asistenciaRepository.countByFecha(LocalDate.now()));
        stats.put("presentes", asistenciaRepository.contarPresentesDeHoy());
        stats.put("ausentes", asistenciaRepository.contarAusentesDeHoy());
        return stats;
    }

    public Map<String, Long> obtenerEstadisticasPorFecha(LocalDate fecha) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", asistenciaRepository.countByFecha(fecha));
        stats.put("presentes", asistenciaRepository.countByFechaAndEstadoAsistencia(fecha, EstadoAsistencia.PRESENTE));
        stats.put("ausentes", asistenciaRepository.countByFechaAndEstadoAsistencia(fecha, EstadoAsistencia.AUSENTE));
        return stats;
    }

    public Map<String, Long> obtenerEstadisticasPorColegio(Long colegioId, LocalDate fecha) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", asistenciaRepository.countByColegioIdAndFecha(colegioId, fecha));
        stats.put("presentes", asistenciaRepository.contarPorColegioEstadoYRango(
                colegioId, fecha, fecha, EstadoAsistencia.PRESENTE));
        stats.put("ausentes", asistenciaRepository.contarPorColegioEstadoYRango(
                colegioId, fecha, fecha, EstadoAsistencia.AUSENTE));
        return stats;
    }

    // ==========================================
    // ESTADÍSTICAS POR ESTUDIANTE
    // ==========================================

    public Map<String, Object> obtenerEstadisticasEstudiante(Long estudianteId) {
        Map<String, Object> stats = new HashMap<>();

        Long totalPresentes = asistenciaRepository.contarPorEstudianteYEstado(
                estudianteId, EstadoAsistencia.PRESENTE);
        Long totalAusentes = asistenciaRepository.contarPorEstudianteYEstado(
                estudianteId, EstadoAsistencia.AUSENTE);
        Long total = totalPresentes + totalAusentes;

        Double porcentaje = asistenciaRepository.calcularPorcentajeAsistenciaEnRango(
                estudianteId, LocalDate.now().minusMonths(1), LocalDate.now());

        stats.put("totalPresentes", totalPresentes);
        stats.put("totalAusentes", totalAusentes);
        stats.put("total", total);
        stats.put("porcentajeAsistencia", porcentaje != null ? porcentaje : 0.0);

        return stats;
    }

    public Map<String, Object> obtenerEstadisticasEstudianteEnRango(Long estudianteId,
                                                                    LocalDate fechaInicio,
                                                                    LocalDate fechaFin) {
        Map<String, Object> stats = new HashMap<>();

        List<Asistencia> asistencias = asistenciaRepository.findByEstudianteIdAndFechaBetween(
                estudianteId, fechaInicio, fechaFin);

        Long totalPresentes = asistencias.stream()
                .filter(a -> a.getEstadoAsistencia() == EstadoAsistencia.PRESENTE)
                .count();

        Long totalAusentes = asistencias.stream()
                .filter(a -> a.getEstadoAsistencia() == EstadoAsistencia.AUSENTE)
                .count();

        Double porcentaje = asistenciaRepository.calcularPorcentajeAsistenciaEnRango(
                estudianteId, fechaInicio, fechaFin);

        stats.put("totalPresentes", totalPresentes);
        stats.put("totalAusentes", totalAusentes);
        stats.put("total", totalPresentes + totalAusentes);
        stats.put("porcentajeAsistencia", porcentaje != null ? porcentaje : 0.0);
        stats.put("fechaInicio", fechaInicio);
        stats.put("fechaFin", fechaFin);

        return stats;
    }

    // ==========================================
    // REPORTES
    // ==========================================

    public List<AsistenciaResponseDTO> generarReportePorColegio(Long colegioId,
                                                                LocalDate fechaInicio,
                                                                LocalDate fechaFin) {
        return asistenciaRepository.reporteAsistenciasPorColegioYRango(colegioId, fechaInicio, fechaFin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AsistenciaResponseDTO> generarReportePorEstudiante(Long estudianteId,
                                                                   LocalDate fechaInicio,
                                                                   LocalDate fechaFin) {
        return asistenciaRepository.reporteAsistenciasPorEstudianteYRango(estudianteId, fechaInicio, fechaFin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    public boolean existeRegistro(Long estudianteId, LocalDate fecha, TipoRecorrido tipoRecorrido) {
        return asistenciaRepository.existsByEstudianteIdAndFechaAndTipoRecorrido(
                estudianteId, fecha, tipoRecorrido);
    }

    // ==========================================
    // CONTADORES
    // ==========================================

    public Long contarPorMonitor(Long monitorId) {
        return asistenciaRepository.countByMonitorId(monitorId);
    }

    public Long contarPorTipoRecorrido(LocalDate fecha, TipoRecorrido tipo) {
        return asistenciaRepository.contarPorFechaYTipo(fecha, tipo);
    }

    public Long contarEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return asistenciaRepository.countByFechaBetween(fechaInicio, fechaFin);
    }

    // ==========================================
    // REGISTRO MASIVO (ÚTIL PARA MONITORES)
    // ==========================================

    public List<AsistenciaResponseDTO> registrarMasivo(List<RegistrarAsistenciaDTO> dtos, Long monitorId) {
        return dtos.stream()
                .map(dto -> {
                    try {
                        return registrar(dto, monitorId);
                    } catch (Exception e) {
                        // Log del error pero continúa con los demás
                        System.err.println("Error al registrar asistencia de estudiante " +
                                dto.getEstudianteId() + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(a -> a != null)
                .collect(Collectors.toList());
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private AsistenciaResponseDTO convertirAResponseDTO(Asistencia asistencia) {
        AsistenciaResponseDTO dto = new AsistenciaResponseDTO();
        dto.setId(asistencia.getId());
        dto.setFecha(asistencia.getFecha());
        dto.setHoraRegistro(asistencia.getHoraRegistro());
        dto.setTipoRecorrido(asistencia.getTipoRecorrido());
        dto.setEstadoAsistencia(asistencia.getEstadoAsistencia());
        dto.setObservaciones(asistencia.getObservaciones());

        // Datos del estudiante
        if (asistencia.getEstudiante() != null) {
            Estudiante estudiante = asistencia.getEstudiante();
            dto.setEstudianteId(estudiante.getId());
            dto.setNombreEstudiante(estudiante.getPrimerNombre() + " " + estudiante.getPrimerApellido());
            dto.setNumIdEstudiante(estudiante.getNumId());
        }

        // Datos del colegio
        if (asistencia.getColegio() != null) {
            dto.setColegioId(asistencia.getColegio().getId());
            dto.setNombreColegio(asistencia.getColegio().getNombreColegio());
        }

        // Datos del monitor
        if (asistencia.getMonitor() != null && asistencia.getMonitor().getUsuario() != null) {
            dto.setMonitorId(asistencia.getMonitor().getId());
            dto.setNombreMonitor(asistencia.getMonitor().getUsuario().getPrimerNombre() + " " +
                    asistencia.getMonitor().getUsuario().getPrimerApellido());
        }

        return dto;
    }

    // Método interno para obtener entidad (usado por otros services si es necesario)
    public Asistencia obtenerEntidadPorId(Long id) {
        return asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada con id: " + id));
    }
}