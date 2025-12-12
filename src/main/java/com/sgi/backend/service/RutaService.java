package com.sgi.backend.service;

import com.sgi.backend.dto.ruta.CrearRutaDTO;
import com.sgi.backend.dto.ruta.ActualizarRutaDTO;
import com.sgi.backend.dto.ruta.RutaResponseDTO;
import com.sgi.backend.model.ColegioJornada;
import com.sgi.backend.model.Ruta;
import com.sgi.backend.model.Zona;
import com.sgi.backend.model.TipoRecorrido;
import com.sgi.backend.repository.ColegioJornadaRepository;
import com.sgi.backend.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RutaService {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private ZonaService zonaService;

    @Autowired
    private ColegioJornadaRepository colegioJornadaRepository;

    // ==========================================
    // CREAR
    // ==========================================

    public RutaResponseDTO crear(CrearRutaDTO dto) {
        // Obtener ColegioJornada
        ColegioJornada colegioJornada = colegioJornadaRepository.findById(dto.getColegioJornadaId())
                .orElseThrow(() -> new RuntimeException("Colegio-Jornada no encontrado"));

        // Obtener datos para construir el nombre
        String nombreColegio = colegioJornada.getColegio().getNombreColegio();
        String nombreJornada = colegioJornada.getJornada().getNombreJornada().getDisplayName();
        String tipoRecorrido = dto.getTipoRuta().name();

        // Construir nombre: "Ciudad Bolivar - Unica REGRESO"
        String nombreRuta = nombreColegio + " - " + nombreJornada + " " + tipoRecorrido;

        // Obtener zona del colegio
        Zona zona = colegioJornada.getColegio().getZona();

        // Validar que no exista una ruta con ese nombre en la misma zona
        if (rutaRepository.existsByNombreRutaAndZonaId(nombreRuta, zona.getId())) {
            throw new RuntimeException("Ya existe la ruta '" + nombreRuta + "' en la zona " + zona.getNombreZona());
        }

        Ruta ruta = new Ruta();
        ruta.setNombreRuta(nombreRuta);
        ruta.setTipoRuta(dto.getTipoRuta());
        ruta.setZona(zona);
        ruta.setActiva(true);

        Ruta rutaGuardada = rutaRepository.save(ruta);
        return convertirAResponseDTO(rutaGuardada);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    public RutaResponseDTO actualizar(Long id, ActualizarRutaDTO dto) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + id));

        // Validar nombre si cambió
        if (!ruta.getNombreRuta().equals(dto.getNombreRuta())) {
            if (rutaRepository.existsByNombreAndZonaAndIdNot(
                    dto.getNombreRuta(),
                    ruta.getZona(),
                    id)) {
                throw new RuntimeException("Ya existe otra ruta con ese nombre en esta zona");
            }
            ruta.setNombreRuta(dto.getNombreRuta());
        }

        ruta.setTipoRuta(dto.getTipoRuta());

        Ruta rutaActualizada = rutaRepository.save(ruta);
        return convertirAResponseDTO(rutaActualizada);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<RutaResponseDTO> listarTodas() {
        return rutaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RutaResponseDTO> listarActivas() {
        return rutaRepository.findByActivaTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RutaResponseDTO> listarPorZona(Long zonaId) {
        return rutaRepository.findByZonaId(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RutaResponseDTO> listarActivasPorZona(Long zonaId) {
        return rutaRepository.findByZonaIdAndActivaTrue(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RutaResponseDTO> listarPorTipo(TipoRecorrido tipoRecorrido) {
        return rutaRepository.findByTipoRuta(tipoRecorrido).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RutaResponseDTO> listarActivasPorTipo(TipoRecorrido tipoRecorrido) {
        return rutaRepository.findByTipoRutaAndActivaTrue(tipoRecorrido).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RutaResponseDTO> listarPorZonaYTipo(Long zonaId, TipoRecorrido tipoRecorrido) {
        return rutaRepository.findByZonaIdAndTipoRuta(zonaId, tipoRecorrido).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RutaResponseDTO> listarActivasPorZonaYTipo(Long zonaId, TipoRecorrido tipoRecorrido) {
        Zona zona = zonaService.obtenerEntidadPorId(zonaId);
        return rutaRepository.findByZonaAndTipoRutaAndActivaTrue(zona, tipoRecorrido).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public RutaResponseDTO obtenerPorId(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + id));
        return convertirAResponseDTO(ruta);
    }

    public List<RutaResponseDTO> buscarPorNombre(String nombre) {
        return rutaRepository.findByNombreRutaContainingIgnoreCase(nombre).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<RutaResponseDTO> buscarPorNombreEnZona(String nombre, Long zonaId) {
        Zona zona = zonaService.obtenerEntidadPorId(zonaId);
        return rutaRepository.findByNombreRutaContainingIgnoreCaseAndZona(nombre, zona).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    public RutaResponseDTO activar(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + id));
        ruta.setActiva(true);
        Ruta rutaActualizada = rutaRepository.save(ruta);
        return convertirAResponseDTO(rutaActualizada);
    }

    public RutaResponseDTO desactivar(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + id));
        ruta.setActiva(false);
        Ruta rutaActualizada = rutaRepository.save(ruta);
        return convertirAResponseDTO(rutaActualizada);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + id));

        // Verificar si tiene estudiantes asociados
        Long totalEstudiantes = rutaRepository.contarEstudiantesPorRuta(id);
        if (totalEstudiantes > 0) {
            throw new RuntimeException("No se puede eliminar la ruta porque tiene " +
                    totalEstudiantes + " estudiantes asociados. Considere desactivarla en su lugar.");
        }

        rutaRepository.deleteById(id);
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    public RutaResponseDTO obtenerConEstadisticas(Long id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + id));

        RutaResponseDTO response = convertirAResponseDTO(ruta);
        response.setTotalEstudiantes(rutaRepository.contarEstudiantesPorRuta(id));

        return response;
    }

    public Long contarEstudiantes(Long rutaId) {
        return rutaRepository.contarEstudiantesPorRuta(rutaId);
    }

    public Long contarEstudiantesActivos(Long rutaId) {
        return rutaRepository.contarEstudiantesActivosPorRuta(rutaId);
    }

    // ==========================================
    // OPERACIONES ESPECIALES
    // ==========================================

    // Obtener rutas de IDA de una zona
    public List<RutaResponseDTO> obtenerRutasIda(Long zonaId) {
        return listarActivasPorZonaYTipo(zonaId, TipoRecorrido.IDA);
    }

    // Obtener rutas de REGRESO de una zona
    public List<RutaResponseDTO> obtenerRutasRegreso(Long zonaId) {
        return listarActivasPorZonaYTipo(zonaId, TipoRecorrido.REGRESO);
    }

    // Clonar una ruta (útil para crear ruta de IDA y REGRESO con mismo nombre)
    public RutaResponseDTO clonar(Long rutaId, TipoRecorrido nuevoTipo) {
        Ruta rutaOriginal = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + rutaId));

        // Validar que no exista ya una ruta con ese nombre y tipo
        String nuevoNombre = rutaOriginal.getNombreRuta();
        if (rutaRepository.existsByNombreRutaAndZonaId(nuevoNombre, rutaOriginal.getZona().getId())) {
            // Si existe, agregar sufijo
            nuevoNombre = nuevoNombre + " - " + nuevoTipo.name();
        }

        Ruta nuevaRuta = new Ruta();
        nuevaRuta.setNombreRuta(nuevoNombre);
        nuevaRuta.setTipoRuta(nuevoTipo);
        nuevaRuta.setZona(rutaOriginal.getZona());
        nuevaRuta.setActiva(true);

        Ruta rutaClonada = rutaRepository.save(nuevaRuta);
        return convertirAResponseDTO(rutaClonada);
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private RutaResponseDTO convertirAResponseDTO(Ruta ruta) {
        RutaResponseDTO dto = new RutaResponseDTO();
        dto.setId(ruta.getId());
        dto.setNombreRuta(ruta.getNombreRuta());
        dto.setTipoRuta(ruta.getTipoRuta());
        dto.setActiva(ruta.getActiva());

        // Datos de zona
        if (ruta.getZona() != null) {
            dto.setZonaId(ruta.getZona().getId());
            dto.setNombreZona(ruta.getZona().getNombreZona());
        }

        // Calcular total de estudiantes
        dto.setTotalEstudiantes(rutaRepository.contarEstudiantesPorRuta(ruta.getId()));

        return dto;
    }

    // Método interno para obtener entidad (usado por otros services)
    public Ruta obtenerEntidadPorId(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con id: " + id));
    }
}
