package com.sgi.backend.repository;

import com.sgi.backend.model.Estudiante;
import com.sgi.backend.model.Colegio;
import com.sgi.backend.model.Jornada;
import com.sgi.backend.model.Ruta;
import com.sgi.backend.model.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // ==========================================
    // BÚSQUEDAS BÁSICAS
    // ==========================================

    // Buscar por número de identificación
    Optional<Estudiante> findByNumId(String numId);

    // Validar si existe por número de identificación (antes de crear)
    boolean existsByNumId(String numId);

    // Buscar por nombre (búsqueda parcial en nombre completo)
    @Query("SELECT e FROM Estudiante e WHERE " +
            "LOWER(CONCAT(e.primerNombre, ' ', COALESCE(e.segundoNombre, ''), ' ', " +
            "e.primerApellido, ' ', COALESCE(e.segundoApellido, ''))) " +
            "LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Estudiante> buscarPorNombre(@Param("nombre") String nombre);

    // Buscar por primer nombre
    List<Estudiante> findByPrimerNombreContainingIgnoreCase(String primerNombre);

    // Buscar por primer apellido
    List<Estudiante> findByPrimerApellidoContainingIgnoreCase(String primerApellido);

    // ==========================================
    // FILTROS POR RELACIONES
    // ==========================================

    // Listar estudiantes por colegio
    List<Estudiante> findByColegio(Colegio colegio);

    // Listar estudiantes por colegio ID
    List<Estudiante> findByColegioId(Long colegioId);

    // Listar estudiantes por jornada
    List<Estudiante> findByJornada(Jornada jornada);

    // Listar estudiantes por jornada ID
    List<Estudiante> findByJornadaId(Long jornadaId);

    // Listar estudiantes por colegio Y jornada (IMPORTANTE PARA MONITORES)
    List<Estudiante> findByColegioAndJornada(Colegio colegio, Jornada jornada);

    // Listar por colegio ID y jornada ID
    List<Estudiante> findByColegioIdAndJornadaId(Long colegioId, Long jornadaId);

    // Listar estudiantes por ruta
    List<Estudiante> findByRuta(Ruta ruta);

    // Listar estudiantes por ruta ID
    List<Estudiante> findByRutaId(Long rutaId);

    // Listar estudiantes por zona (a través de colegio)
    @Query("SELECT e FROM Estudiante e WHERE e.colegio.zona.id = :zonaId")
    List<Estudiante> findByZonaId(@Param("zonaId") Long zonaId);

    // ==========================================
    // FILTROS DE ESTADO
    // ==========================================

    // Listar solo estudiantes activos
    List<Estudiante> findByActivoTrue();

    // Listar estudiantes activos por colegio
    List<Estudiante> findByColegioAndActivoTrue(Colegio colegio);

    // Listar estudiantes activos por colegio ID
    List<Estudiante> findByColegioIdAndActivoTrue(Long colegioId);

    // Listar estudiantes activos por colegio Y jornada
    List<Estudiante> findByColegioAndJornadaAndActivoTrue(Colegio colegio, Jornada jornada);

    // Listar estudiantes activos por colegio ID y jornada ID
    List<Estudiante> findByColegioIdAndJornadaIdAndActivoTrue(Long colegioId, Long jornadaId);

    // Filtrar por estado de inscripción
    List<Estudiante> findByEstadoInscripcion(String estadoInscripcion);

    // Listar estudiantes por estado de inscripción (usando enum si lo cambias)
    // List<Estudiante> findByEstadoInscripcion(EstadoInscripcion estadoInscripcion);

    // Estudiantes activos por estado de inscripción
    List<Estudiante> findByEstadoInscripcionAndActivoTrue(String estadoInscripcion);

    // ==========================================
    // QUERY ESPECIAL PARA MONITORES (LA MÁS IMPORTANTE)
    // ==========================================

    // Obtener estudiantes que puede ver un monitor (por zona y jornada)
    @Query("SELECT e FROM Estudiante e " +
            "WHERE e.colegio.zona.id = :zonaId " +
            "AND e.jornada.id = :jornadaId " +
            "AND e.activo = true")
    List<Estudiante> findEstudiantesParaMonitor(@Param("zonaId") Long zonaId,
                                                @Param("jornadaId") Long jornadaId);

    // Estudiantes de monitor con estado de inscripción específico
    @Query("SELECT e FROM Estudiante e " +
            "WHERE e.colegio.zona.id = :zonaId " +
            "AND e.jornada.id = :jornadaId " +
            "AND e.activo = true " +
            "AND e.estadoInscripcion = :estado")
    List<Estudiante> findEstudiantesParaMonitorPorEstado(@Param("zonaId") Long zonaId,
                                                         @Param("jornadaId") Long jornadaId,
                                                         @Param("estado") String estado);

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    // Contar estudiantes por colegio
    Long countByColegio(Colegio colegio);

    // Contar estudiantes por colegio ID
    Long countByColegioId(Long colegioId);

    // Contar estudiantes activos por colegio
    Long countByColegioAndActivoTrue(Colegio colegio);

    // Contar estudiantes por ruta
    Long countByRuta(Ruta ruta);

    // Contar estudiantes por ruta ID
    Long countByRutaId(Long rutaId);

    // Contar estudiantes por estado de inscripción
    Long countByEstadoInscripcion(String estadoInscripcion);

    // Contar estudiantes de un monitor
    @Query("SELECT COUNT(e) FROM Estudiante e " +
            "WHERE e.colegio.zona.id = :zonaId " +
            "AND e.jornada.id = :jornadaId " +
            "AND e.activo = true")
    Long contarEstudiantesDeMonitor(@Param("zonaId") Long zonaId,
                                    @Param("jornadaId") Long jornadaId);

    // ==========================================
    // REPORTES Y FECHAS
    // ==========================================

    // Buscar estudiantes inscritos después de una fecha
    List<Estudiante> findByFechaInscripcionAfter(LocalDate fecha);

    // Buscar estudiantes inscritos antes de una fecha
    List<Estudiante> findByFechaInscripcionBefore(LocalDate fecha);

    // Buscar estudiantes inscritos en un rango de fechas
    List<Estudiante> findByFechaInscripcionBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Estudiantes inscritos en el último mes (ejemplo)
    @Query("SELECT e FROM Estudiante e WHERE e.fechaInscripcion >= :fechaLimite")
    List<Estudiante> findEstudiantesInscritosRecientes(@Param("fechaLimite") LocalDate fechaLimite);

    // ==========================================
    // ASISTENCIAS POR ESTUDIANTE
    // ==========================================

    // Contar asistencias de un estudiante
    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.estudiante.id = :estudianteId")
    Long contarAsistenciasPorEstudiante(@Param("estudianteId") Long estudianteId);

    // Contar asistencias por estado (PRESENTE, AUSENTE)
    @Query("SELECT COUNT(a) FROM Asistencia a " +
            "WHERE a.estudiante.id = :estudianteId " +
            "AND a.estadoAsistencia = :estado")
    Long contarAsistenciasPorEstadoYEstudiante(@Param("estudianteId") Long estudianteId,
                                               @Param("estado") String estado);

    // Contar asistencias de un estudiante en un rango de fechas
    @Query("SELECT COUNT(a) FROM Asistencia a " +
            "WHERE a.estudiante.id = :estudianteId " +
            "AND a.fecha BETWEEN :fechaInicio AND :fechaFin")
    Long contarAsistenciasPorEstudianteYRangoFechas(@Param("estudianteId") Long estudianteId,
                                                    @Param("fechaInicio") LocalDate fechaInicio,
                                                    @Param("fechaFin") LocalDate fechaFin);

    // Porcentaje de asistencia de un estudiante
    @Query("SELECT " +
            "(COUNT(CASE WHEN a.estadoAsistencia = 'PRESENTE' THEN 1 END) * 100.0 / COUNT(*)) " +
            "FROM Asistencia a WHERE a.estudiante.id = :estudianteId")
    Double calcularPorcentajeAsistencia(@Param("estudianteId") Long estudianteId);

    // ==========================================
    // VALIDACIONES PARA ACTUALIZACIÓN
    // ==========================================

    // Validar si existe numId en otro estudiante (para actualización)
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Estudiante e WHERE e.numId = :numId AND e.id != :estudianteId")
    boolean existsByNumIdAndIdNot(@Param("numId") String numId,
                                  @Param("estudianteId") Long estudianteId);
}