package com.sgi.backend.repository;

import com.sgi.backend.model.ColegioJornada;
import com.sgi.backend.model.Colegio;
import com.sgi.backend.model.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ColegioJornadaRepository extends JpaRepository<ColegioJornada, Long> {

    // Listar todas las jornadas de un colegio
    List<ColegioJornada> findByColegio(Colegio colegio);

    // Listar jornadas por colegio ID
    List<ColegioJornada> findByColegioId(Long colegioId);

    // Listar solo las jornadas activas de un colegio
    List<ColegioJornada> findByColegioAndActivaTrue(Colegio colegio);

    // Listar jornadas activas por colegio ID
    List<ColegioJornada> findByColegioIdAndActivaTrue(Long colegioId);

    // Listar todos los colegios que tienen una jornada específica
    List<ColegioJornada> findByJornada(Jornada jornada);

    // Listar colegios por jornada ID
    List<ColegioJornada> findByJornadaId(Long jornadaId);

    // Listar colegios activos que tienen una jornada específica
    List<ColegioJornada> findByJornadaAndActivaTrue(Jornada jornada);

    // Buscar relación específica colegio-jornada
    Optional<ColegioJornada> findByColegioAndJornada(Colegio colegio, Jornada jornada);

    // Buscar por IDs
    Optional<ColegioJornada> findByColegioIdAndJornadaId(Long colegioId, Long jornadaId);

    // Verificar si existe la relación colegio-jornada (antes de crear)
    boolean existsByColegioAndJornada(Colegio colegio, Jornada jornada);

    // Verificar por IDs
    boolean existsByColegioIdAndJornadaId(Long colegioId, Long jornadaId);

    // Verificar si existe relación activa
    boolean existsByColegioAndJornadaAndActivaTrue(Colegio colegio, Jornada jornada);

    // Contar cuántas jornadas tiene un colegio (todas)
    Long countByColegio(Colegio colegio);

    // Contar cuántas jornadas activas tiene un colegio
    Long countByColegioAndActivaTrue(Colegio colegio);

    // Contar por colegio ID
    Long countByColegioId(Long colegioId);

    // Contar jornadas activas por colegio ID
    Long countByColegioIdAndActivaTrue(Long colegioId);

    // Activar una jornada de un colegio
    @Modifying
    @Query("UPDATE ColegioJornada cj SET cj.activa = true " +
            "WHERE cj.colegio.id = :colegioId AND cj.jornada.id = :jornadaId")
    int activarJornada(@Param("colegioId") Long colegioId, @Param("jornadaId") Long jornadaId);

    // Desactivar una jornada de un colegio
    @Modifying
    @Query("UPDATE ColegioJornada cj SET cj.activa = false " +
            "WHERE cj.colegio.id = :colegioId AND cj.jornada.id = :jornadaId")
    int desactivarJornada(@Param("colegioId") Long colegioId, @Param("jornadaId") Long jornadaId);

    // Eliminar relación específica
    void deleteByColegioAndJornada(Colegio colegio, Jornada jornada);

    // Eliminar por IDs
    void deleteByColegioIdAndJornadaId(Long colegioId, Long jornadaId);
}