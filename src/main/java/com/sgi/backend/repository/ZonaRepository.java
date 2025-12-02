package com.sgi.backend.repository;

import com.sgi.backend.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ZonaRepository extends JpaRepository<Zona, Long> {

    // Buscar por código de zona (ej: "Z001")
    Optional<Zona> findByCodigoZona(String codigoZona);

    // Buscar por nombre exacto
    Optional<Zona> findByNombreZona(String nombreZona);

    // Buscar por nombre (búsqueda parcial, ignora mayúsculas/minúsculas)
    List<Zona> findByNombreZonaContainingIgnoreCase(String nombreZona);

    // Listar solo zonas activas
    List<Zona> findByActivaTrue();

    // Validar si existe por código (antes de crear)
    boolean existsByCodigoZona(String codigoZona);

    // Validar si existe por nombre (antes de crear)
    boolean existsByNombreZona(String nombreZona);

    // Contar cuántos colegios tiene una zona
    @Query("SELECT COUNT(c) FROM Colegio c WHERE c.zona.id = :zonaId")
    Long contarColegiosPorZona(@Param("zonaId") Long zonaId);

    // Contar cuántos monitores tiene una zona
    @Query("SELECT COUNT(m) FROM Monitor m WHERE m.zona.id = :zonaId")
    Long contarMonitoresPorZona(@Param("zonaId") Long zonaId);

    // Contar cuántas jornadas tiene una zona
    @Query("SELECT COUNT(j) FROM Jornada j WHERE j.zona.id = :zonaId")
    Long contarJornadasPorZona(@Param("zonaId") Long zonaId);

    // Contar cuántas rutas tiene una zona
    @Query("SELECT COUNT(r) FROM Ruta r WHERE r.zona.id = :zonaId")
    Long contarRutasPorZona(@Param("zonaId") Long zonaId);
}
