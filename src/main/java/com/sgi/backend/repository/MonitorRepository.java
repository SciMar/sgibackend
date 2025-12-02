package com.sgi.backend.repository;

import com.sgi.backend.model.Monitor;
import com.sgi.backend.model.Usuario;
import com.sgi.backend.model.Zona;
import com.sgi.backend.model.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    // Buscar monitor por usuario
    Optional<Monitor> findByUsuario(Usuario usuario);

    // Buscar por usuario ID (más directo)
    Optional<Monitor> findByUsuarioId(Long usuarioId);

    // Listar todos los monitores de una zona
    List<Monitor> findByZona(Zona zona);

    // Listar monitores por zona ID
    List<Monitor> findByZonaId(Long zonaId);

    // Listar monitores de una zona Y jornada específicas
    List<Monitor> findByZonaAndJornada(Zona zona, Jornada jornada);

    // Listar monitores por zona ID y jornada ID
    List<Monitor> findByZonaIdAndJornadaId(Long zonaId, Long jornadaId);

    // Listar solo monitores activos
    List<Monitor> findByActivoTrue();

    // Listar monitores activos por zona
    List<Monitor> findByZonaAndActivoTrue(Zona zona);

    // Listar monitores activos por zona ID
    List<Monitor> findByZonaIdAndActivoTrue(Long zonaId);

    // Listar monitores activos por zona y jornada
    List<Monitor> findByZonaAndJornadaAndActivoTrue(Zona zona, Jornada jornada);

    // Validar si ya existe monitor para un usuario (antes de crear)
    boolean existsByUsuarioId(Long usuarioId);

    // Contar cuántas asistencias ha registrado un monitor
    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.monitor.id = :monitorId")
    Long contarAsistenciasPorMonitor(@Param("monitorId") Long monitorId);

    // Buscar monitores por fecha de asignación
    List<Monitor> findByFechaAsignacion(LocalDate fechaAsignacion);

    // Buscar monitores asignados después de una fecha
    List<Monitor> findByFechaAsignacionAfter(LocalDate fecha);

    // Buscar monitores asignados antes de una fecha
    List<Monitor> findByFechaAsignacionBefore(LocalDate fecha);

    // Buscar monitores asignados en un rango de fechas
    List<Monitor> findByFechaAsignacionBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar monitores asignados en el último mes
    @Query("SELECT m FROM Monitor m WHERE m.fechaAsignacion >= :fechaLimite")
    List<Monitor> findMonitoresAsignadosRecientes(@Param("fechaLimite") LocalDate fechaLimite);

    // Verificar si existe monitor activo para zona y jornada
    boolean existsByZonaAndJornadaAndActivoTrue(Zona zona, Jornada jornada);
}
