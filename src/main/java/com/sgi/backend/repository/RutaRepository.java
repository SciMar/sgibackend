package com.sgi.backend.repository;

import com.sgi.backend.model.Ruta;
import com.sgi.backend.model.TipoRecorrido;
import com.sgi.backend.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    // Buscar rutas por zona
    List<Ruta> findByZona(Zona zona);

    // Buscar rutas por zona ID
    List<Ruta> findByZonaId(Long zonaId);

    // Buscar rutas por tipo (IDA, REGRESO)
    List<Ruta> findByTipoRuta(TipoRecorrido tipoRuta);

    // Buscar rutas por zona Y tipo combinados
    List<Ruta> findByZonaAndTipoRuta(Zona zona, TipoRecorrido tipoRuta);

    // Buscar por zona ID y tipo
    List<Ruta> findByZonaIdAndTipoRuta(Long zonaId, TipoRecorrido tipoRuta);

    // Listar solo rutas activas
    List<Ruta> findByActivaTrue();

    // Listar rutas activas por zona
    List<Ruta> findByZonaAndActivaTrue(Zona zona);

    // Listar rutas activas por zona ID
    List<Ruta> findByZonaIdAndActivaTrue(Long zonaId);

    // Listar rutas activas por tipo
    List<Ruta> findByTipoRutaAndActivaTrue(TipoRecorrido tipoRuta);

    // Listar rutas activas por zona y tipo
    List<Ruta> findByZonaAndTipoRutaAndActivaTrue(Zona zona, TipoRecorrido tipoRuta);

    // Buscar por nombre de ruta (búsqueda parcial)
    List<Ruta> findByNombreRutaContainingIgnoreCase(String nombreRuta);

    // Buscar por nombre exacto
    Optional<Ruta> findByNombreRuta(String nombreRuta);

    // Buscar por nombre en una zona específica
    List<Ruta> findByNombreRutaContainingIgnoreCaseAndZona(String nombreRuta, Zona zona);

    // Validar si ya existe una ruta con ese nombre en una zona
    boolean existsByNombreRutaAndZona(String nombreRuta, Zona zona);

    // Validar por nombre y zona ID
    boolean existsByNombreRutaAndZonaId(String nombreRuta, Long zonaId);

    // Contar cuántos estudiantes tiene una ruta
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.ruta.id = :rutaId")
    Long contarEstudiantesPorRuta(@Param("rutaId") Long rutaId);

    // Contar cuántos estudiantes activos tiene una ruta
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.ruta.id = :rutaId AND e.activo = true")
    Long contarEstudiantesActivosPorRuta(@Param("rutaId") Long rutaId);

    // Validar si existe por nombre en otra ruta (para actualización)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Ruta r WHERE r.nombreRuta = :nombre AND r.zona = :zona AND r.id != :rutaId")
    boolean existsByNombreAndZonaAndIdNot(@Param("nombre") String nombre,
                                          @Param("zona") Zona zona,
                                          @Param("rutaId") Long rutaId);
}
