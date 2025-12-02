package com.sgi.backend.repository;

import com.sgi.backend.model.Jornada;
import com.sgi.backend.model.TipoJornada;
import com.sgi.backend.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Long> {

    // Buscar por código de jornada
    Optional<Jornada> findByCodigoJornada(String codigoJornada);

    // Validar si existe por código (antes de crear)
    boolean existsByCodigoJornada(String codigoJornada);

    // Buscar todas las jornadas de una zona
    List<Jornada> findByZona(Zona zona);

    // Buscar por zona ID
    List<Jornada> findByZonaId(Long zonaId);

    // Buscar jornadas activas de una zona específica
    List<Jornada> findByZonaAndActivaTrue(Zona zona);

    // Buscar jornadas activas por zona ID
    List<Jornada> findByZonaIdAndActivaTrue(Long zonaId);

    // Filtrar jornadas por tipo (MANANA, TARDE, UNICA)
    List<Jornada> findByNombreJornada(TipoJornada tipoJornada);

    // Buscar jornadas por tipo Y zona combinados
    List<Jornada> findByZonaAndNombreJornada(Zona zona, TipoJornada tipoJornada);

    // Buscar jornadas por tipo Y zona ID combinados
    List<Jornada> findByZonaIdAndNombreJornada(Long zonaId, TipoJornada tipoJornada);

    // Buscar jornadas activas por tipo
    List<Jornada> findByNombreJornadaAndActivaTrue(TipoJornada tipoJornada);

    // Listar solo jornadas activas
    List<Jornada> findByActivaTrue();

    // Contar cuántos estudiantes tiene una jornada
    @Query("SELECT COUNT(e) FROM Estudiante e WHERE e.jornada.id = :jornadaId")
    Long contarEstudiantesPorJornada(@Param("jornadaId") Long jornadaId);

    // Contar cuántos monitores tiene una jornada
    @Query("SELECT COUNT(m) FROM Monitor m WHERE m.jornada.id = :jornadaId")
    Long contarMonitoresPorJornada(@Param("jornadaId") Long jornadaId);

    // Validar si existe combinación zona + tipo de jornada
    boolean existsByZonaAndNombreJornada(Zona zona, TipoJornada tipoJornada);
}
