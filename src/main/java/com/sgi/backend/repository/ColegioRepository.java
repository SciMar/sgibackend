package com.sgi.backend.repository;

import com.sgi.backend.model.Colegio;
import com.sgi.backend.model.Zona;
import com.sgi.backend.model.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ColegioRepository extends JpaRepository<Colegio, Long> {

    // Buscar colegios por zona
    List<Colegio> findByZona(Zona zona);

    // Buscar colegios por zona ID
    List<Colegio> findByZonaId(Long zonaId);

    // Buscar colegios activos por zona
    List<Colegio> findByZonaAndActivoTrue(Zona zona);

    // Buscar colegios activos por zona ID
    List<Colegio> findByZonaIdAndActivoTrue(Long zonaId);

    // Buscar por nombre (búsqueda parcial, ignora mayúsculas/minúsculas)
    List<Colegio> findByNombreColegioContainingIgnoreCase(String nombreColegio);

    // Buscar por nombre exacto
    Optional<Colegio> findByNombreColegio(String nombreColegio);

    // Listar solo colegios activos
    List<Colegio> findByActivoTrue();

    // Validar si ya existe un colegio con ese nombre en una zona
    boolean existsByNombreColegioAndZona(String nombreColegio, Zona zona);

    // Validar si existe por nombre y zona ID
    boolean existsByNombreColegioAndZonaId(String nombreColegio, Long zonaId);

    // Contar cuántos estudiantes tiene un colegio
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.colegio.id = :colegioId")
    Long contarEstudiantesPorColegio(@Param("colegioId") Long colegioId);

    // Contar estudiantes activos por colegio
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.colegio.id = :colegioId AND e.activo = true")
    Long contarEstudiantesActivosPorColegio(@Param("colegioId") Long colegioId);

    // Listar colegios que tienen una jornada específica
    @Query("SELECT DISTINCT c FROM Colegio c " +
            "JOIN c.colegioJornadas cj " +
            "WHERE cj.jornada = :jornada AND cj.activa = true")
    List<Colegio> findColegiosConJornada(@Param("jornada") Jornada jornada);

    // Listar colegios que tienen una jornada específica por ID
    @Query("SELECT DISTINCT c FROM Colegio c " +
            "JOIN c.colegioJornadas cj " +
            "WHERE cj.jornada.id = :jornadaId AND cj.activa = true")
    List<Colegio> findColegiosConJornadaId(@Param("jornadaId") Long jornadaId);

    // Listar colegios activos que tienen una jornada específica
    @Query("SELECT DISTINCT c FROM Colegio c " +
            "JOIN c.colegioJornadas cj " +
            "WHERE cj.jornada = :jornada AND cj.activa = true AND c.activo = true")
    List<Colegio> findColegiosActivosConJornada(@Param("jornada") Jornada jornada);

    // Buscar colegios por nombre en una zona específica
    List<Colegio> findByNombreColegioContainingIgnoreCaseAndZona(String nombreColegio, Zona zona);

    // Validar si existe por nombre en otro colegio (para actualización)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Colegio c WHERE c.nombreColegio = :nombre AND c.zona = :zona AND c.id != :colegioId")
    boolean existsByNombreAndZonaAndIdNot(@Param("nombre") String nombre,
                                          @Param("zona") Zona zona,
                                          @Param("colegioId") Long colegioId);
}
