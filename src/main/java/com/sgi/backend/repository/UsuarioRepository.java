package com.sgi.backend.repository;

import com.sgi.backend.model.Usuario;
import com.sgi.backend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por email (para login)
    Optional<Usuario> findByEmail(String email);

    // Buscar por número de identificación
    Optional<Usuario> findByNumId(String numId);

    // Filtrar usuarios por rol
    List<Usuario> findByRol(Rol rol);

    // Listar solo usuarios activos
    List<Usuario> findByActivoTrue();

    // Listar usuarios activos por rol
    List<Usuario> findByRolAndActivoTrue(Rol rol);

    // Validar si ya existe un email (antes de crear)
    boolean existsByEmail(String email);

    // Validar si ya existe un número de ID (antes de crear)
    boolean existsByNumId(String numId);

    // Buscar por nombre (búsqueda parcial en primer nombre)
    List<Usuario> findByPrimerNombreContainingIgnoreCase(String nombre);

    // Buscar por apellido (búsqueda parcial en primer apellido)
    List<Usuario> findByPrimerApellidoContainingIgnoreCase(String apellido);

    // Buscar por nombre completo (nombre O apellido)
    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.primerNombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "LOWER(u.primerApellido) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Usuario> buscarPorNombreOApellido(@Param("texto") String texto);

    // Contar cuántos usuarios hay por rol
    Long countByRol(Rol rol);

    // Contar usuarios activos por rol
    Long countByRolAndActivoTrue(Rol rol);

    // Validar si existe email en otro usuario (para actualización)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM Usuario u WHERE u.email = :email AND u.id != :usuarioId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("usuarioId") Long usuarioId);

    // Validar si existe numId en otro usuario (para actualización)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM Usuario u WHERE u.numId = :numId AND u.id != :usuarioId")
    boolean existsByNumIdAndIdNot(@Param("numId") String numId, @Param("usuarioId") Long usuarioId);
}
