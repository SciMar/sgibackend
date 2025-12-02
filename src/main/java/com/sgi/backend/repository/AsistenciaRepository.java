package com.sgi.backend.repository;

import com.sgi.backend.model.Asistencia;
import com.sgi.backend.model.Estudiante;
import com.sgi.backend.model.Colegio;
import com.sgi.backend.model.Monitor;
import com.sgi.backend.model.TipoRecorrido;
import com.sgi.backend.model.EstadoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // ==========================================
    // BÚSQUEDAS POR ESTUDIANTE
    // ==========================================

    // Listar todas las asistencias de un estudiante
    List<Asistencia> findByEstudiante(Estudiante estudiante);

    // Listar asistencias por estudiante ID
    List<Asistencia> findByEstudianteId(Long estudianteId);

    // Asistencias de un estudiante en una fecha específica
    List<Asistencia> findByEstudianteAndFecha(Estudiante estudiante, LocalDate fecha);

    // Asistencias por estudiante ID y fecha
    List<Asistencia> findByEstudianteIdAndFecha(Long estudianteId, LocalDate fecha);

    // Asistencias de un estudiante en un rango de fechas
    List<Asistencia> findByEstudianteAndFechaBetween(Estudiante estudiante,
                                                     LocalDate fechaInicio,
                                                     LocalDate fechaFin);

    // Por estudiante ID en rango de fechas
    List<Asistencia> findByEstudianteIdAndFechaBetween(Long estudianteId,
                                                       LocalDate fechaInicio,
                                                       LocalDate fechaFin);

    // ==========================================
    // BÚSQUEDAS POR FECHA
    // ==========================================

    // Listar asistencias de un día específico
    List<Asistencia> findByFecha(LocalDate fecha);

    // Asistencias de hoy
    @Query("SELECT a FROM Asistencia a WHERE a.fecha = CURRENT_DATE")
    List<Asistencia> findAsistenciasDeHoy();

    // Asistencias en un rango de fechas
    List<Asistencia> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // ==========================================
    // BÚSQUEDAS POR COLEGIO
    // ==========================================

    // Asistencias por colegio y fecha
    List<Asistencia> findByColegioAndFecha(Colegio colegio, LocalDate fecha);

    // Por colegio ID y fecha
    List<Asistencia> findByColegioIdAndFecha(Long colegioId, LocalDate fecha);

    // Asistencias por colegio en rango de fechas
    List<Asistencia> findByColegioAndFechaBetween(Colegio colegio,
                                                  LocalDate fechaInicio,
                                                  LocalDate fechaFin);

    // Por colegio ID en rango de fechas
    List<Asistencia> findByColegioIdAndFechaBetween(Long colegioId,
                                                    LocalDate fechaInicio,
                                                    LocalDate fechaFin);

    // ==========================================
    // BÚSQUEDAS POR MONITOR
    // ==========================================

    // Asistencias registradas por un monitor
    List<Asistencia> findByMonitor(Monitor monitor);

    // Por monitor ID
    List<Asistencia> findByMonitorId(Long monitorId);

    // Asistencias de un monitor en una fecha
    List<Asistencia> findByMonitorAndFecha(Monitor monitor, LocalDate fecha);

    // Por monitor ID y fecha
    List<Asistencia> findByMonitorIdAndFecha(Long monitorId, LocalDate fecha);

    // Asistencias de un monitor en rango de fechas
    List<Asistencia> findByMonitorIdAndFechaBetween(Long monitorId,
                                                    LocalDate fechaInicio,
                                                    LocalDate fechaFin);

    // ==========================================
    // FILTROS POR TIPO Y ESTADO
    // ==========================================

    // Por tipo de recorrido (IDA, REGRESO)
    List<Asistencia> findByTipoRecorrido(TipoRecorrido tipoRecorrido);

    // Por tipo de recorrido y fecha
    List<Asistencia> findByTipoRecorridoAndFecha(TipoRecorrido tipoRecorrido, LocalDate fecha);

    // Por estado de asistencia (PRESENTE, AUSENTE)
    List<Asistencia> findByEstadoAsistencia(EstadoAsistencia estadoAsistencia);

    // Por estado y fecha
    List<Asistencia> findByEstadoAsistenciaAndFecha(EstadoAsistencia estadoAsistencia,
                                                    LocalDate fecha);

    // Por fecha, tipo y estado (combinación completa)
    List<Asistencia> findByFechaAndTipoRecorridoAndEstadoAsistencia(LocalDate fecha,
                                                                    TipoRecorrido tipoRecorrido,
                                                                    EstadoAsistencia estadoAsistencia);

    // ==========================================
    // VALIDACIONES
    // ==========================================

    // Buscar asistencia específica (estudiante + fecha + tipo)
    Optional<Asistencia> findByEstudianteAndFechaAndTipoRecorrido(Estudiante estudiante,
                                                                  LocalDate fecha,
                                                                  TipoRecorrido tipoRecorrido);

    // Por IDs
    Optional<Asistencia> findByEstudianteIdAndFechaAndTipoRecorrido(Long estudianteId,
                                                                    LocalDate fecha,
                                                                    TipoRecorrido tipoRecorrido);

    // Verificar si existe asistencia (evitar duplicados)
    boolean existsByEstudianteAndFechaAndTipoRecorrido(Estudiante estudiante,
                                                       LocalDate fecha,
                                                       TipoRecorrido tipoRecorrido);

    // Por IDs
    boolean existsByEstudianteIdAndFechaAndTipoRecorrido(Long estudianteId,
                                                         LocalDate fecha,
                                                         TipoRecorrido tipoRecorrido);

    // ==========================================
    // ESTADÍSTICAS Y REPORTES
    // ==========================================

    // Contar asistencias por estado en un día específico
    Long countByFechaAndEstadoAsistencia(LocalDate fecha, EstadoAsistencia estadoAsistencia);

    // Contar presentes de hoy
    @Query("SELECT COUNT(a) FROM Asistencia a " +
            "WHERE a.fecha = CURRENT_DATE AND a.estadoAsistencia = 'PRESENTE'")
    Long contarPresentesDeHoy();

    // Contar ausentes de hoy
    @Query("SELECT COUNT(a) FROM Asistencia a " +
            "WHERE a.fecha = CURRENT_DATE AND a.estadoAsistencia = 'AUSENTE'")
    Long contarAusentesDeHoy();

    // Contar asistencias por colegio y fecha
    Long countByColegioAndFecha(Colegio colegio, LocalDate fecha);

    // Contar por colegio ID y fecha
    Long countByColegioIdAndFecha(Long colegioId, LocalDate fecha);

    // Contar asistencias de un estudiante por estado
    @Query("SELECT COUNT(a) FROM Asistencia a " +
            "WHERE a.estudiante.id = :estudianteId " +
            "AND a.estadoAsistencia = :estado")
    Long contarPorEstudianteYEstado(@Param("estudianteId") Long estudianteId,
                                    @Param("estado") EstadoAsistencia estado);

    // Contar asistencias en rango de fechas
    Long countByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Contar por fecha
    Long countByFecha(LocalDate fecha);

    // ==========================================
    // REPORTES AVANZADOS
    // ==========================================

    // Reporte de asistencias por colegio en rango de fechas
    @Query("SELECT a FROM Asistencia a " +
            "WHERE a.colegio.id = :colegioId " +
            "AND a.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY a.fecha DESC, a.horaRegistro DESC")
    List<Asistencia> reporteAsistenciasPorColegioYRango(@Param("colegioId") Long colegioId,
                                                        @Param("fechaInicio") LocalDate fechaInicio,
                                                        @Param("fechaFin") LocalDate fechaFin);

    // Reporte por estudiante en rango de fechas
    @Query("SELECT a FROM Asistencia a " +
            "WHERE a.estudiante.id = :estudianteId " +
            "AND a.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY a.fecha DESC")
    List<Asistencia> reporteAsistenciasPorEstudianteYRango(@Param("estudianteId") Long estudianteId,
                                                           @Param("fechaInicio") LocalDate fechaInicio,
                                                           @Param("fechaFin") LocalDate fechaFin);

    // Estadísticas por colegio y estado en rango
    @Query("SELECT COUNT(a) FROM Asistencia a " +
            "WHERE a.colegio.id = :colegioId " +
            "AND a.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "AND a.estadoAsistencia = :estado")
    Long contarPorColegioEstadoYRango(@Param("colegioId") Long colegioId,
                                      @Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin,
                                      @Param("estado") EstadoAsistencia estado);

    // Porcentaje de asistencia de un estudiante en un rango
    @Query("SELECT " +
            "(COUNT(CASE WHEN a.estadoAsistencia = 'PRESENTE' THEN 1 END) * 100.0 / COUNT(*)) " +
            "FROM Asistencia a " +
            "WHERE a.estudiante.id = :estudianteId " +
            "AND a.fecha BETWEEN :fechaInicio AND :fechaFin")
    Double calcularPorcentajeAsistenciaEnRango(@Param("estudianteId") Long estudianteId,
                                               @Param("fechaInicio") LocalDate fechaInicio,
                                               @Param("fechaFin") LocalDate fechaFin);

    // Asistencias de una zona en una fecha (para monitores/supervisores)
    @Query("SELECT a FROM Asistencia a " +
            "WHERE a.colegio.zona.id = :zonaId " +
            "AND a.fecha = :fecha")
    List<Asistencia> findByZonaIdAndFecha(@Param("zonaId") Long zonaId,
                                          @Param("fecha") LocalDate fecha);

    // Total de asistencias registradas por un monitor
    Long countByMonitor(Monitor monitor);

    // Por monitor ID
    Long countByMonitorId(Long monitorId);

    // Asistencias del día por tipo de recorrido
    @Query("SELECT COUNT(a) FROM Asistencia a " +
            "WHERE a.fecha = :fecha " +
            "AND a.tipoRecorrido = :tipo")
    Long contarPorFechaYTipo(@Param("fecha") LocalDate fecha,
                             @Param("tipo") TipoRecorrido tipo);


}
