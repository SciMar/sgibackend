package com.sgi.backend.service;

import com.sgi.backend.dto.colegio.CrearColegioDTO;
import com.sgi.backend.dto.colegio.ActualizarColegioDTO;
import com.sgi.backend.dto.colegio.ColegioResponseDTO;
import com.sgi.backend.model.Colegio;
import com.sgi.backend.model.Zona;
import com.sgi.backend.repository.ColegioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ColegioService {

    @Autowired
    private ColegioRepository colegioRepository;

    @Autowired
    private ZonaService zonaService;

    // ==========================================
    // CREAR
    // ==========================================

    public ColegioResponseDTO crear(CrearColegioDTO dto) {
        // Obtener la zona
        Zona zona = zonaService.obtenerEntidadPorId(dto.getZonaId());

        // Validar que no exista un colegio con ese nombre en la misma zona
        if (colegioRepository.existsByNombreColegioAndZonaId(dto.getNombreColegio(), dto.getZonaId())) {
            throw new RuntimeException("Ya existe un colegio con el nombre '" + dto.getNombreColegio() +
                    "' en la zona " + zona.getNombreZona());
        }

        Colegio colegio = new Colegio();
        colegio.setNombreColegio(dto.getNombreColegio());
        colegio.setZona(zona);
        colegio.setActivo(true);

        Colegio colegioGuardado = colegioRepository.save(colegio);
        return convertirAResponseDTO(colegioGuardado);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    public ColegioResponseDTO actualizar(Long id, ActualizarColegioDTO dto) {
        Colegio colegio = colegioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado con id: " + id));

        // Validar nombre si cambió
        if (!colegio.getNombreColegio().equals(dto.getNombreColegio())) {
            if (colegioRepository.existsByNombreAndZonaAndIdNot(
                    dto.getNombreColegio(),
                    colegio.getZona(),
                    id)) {
                throw new RuntimeException("Ya existe otro colegio con ese nombre en esta zona");
            }
            colegio.setNombreColegio(dto.getNombreColegio());
        }

        Colegio colegioActualizado = colegioRepository.save(colegio);
        return convertirAResponseDTO(colegioActualizado);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<ColegioResponseDTO> listarTodos() {
        return colegioRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioResponseDTO> listarActivos() {
        return colegioRepository.findByActivoTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioResponseDTO> listarPorZona(Long zonaId) {
        return colegioRepository.findByZonaId(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioResponseDTO> listarActivosPorZona(Long zonaId) {
        return colegioRepository.findByZonaIdAndActivoTrue(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioResponseDTO> listarPorJornada(Long jornadaId) {
        return colegioRepository.findColegiosConJornadaId(jornadaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioResponseDTO> listarActivosPorJornada(Long jornadaId) {
        // Obtener colegios que tienen esta jornada y están activos
        return colegioRepository.findColegiosConJornadaId(jornadaId).stream()
                .filter(Colegio::getActivo)
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public ColegioResponseDTO obtenerPorId(Long id) {
        Colegio colegio = colegioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado con id: " + id));
        return convertirAResponseDTO(colegio);
    }

    public List<ColegioResponseDTO> buscarPorNombre(String nombre) {
        return colegioRepository.findByNombreColegioContainingIgnoreCase(nombre).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioResponseDTO> buscarPorNombreEnZona(String nombre, Long zonaId) {
        Zona zona = zonaService.obtenerEntidadPorId(zonaId);
        return colegioRepository.findByNombreColegioContainingIgnoreCaseAndZona(nombre, zona).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    public ColegioResponseDTO activar(Long id) {
        Colegio colegio = colegioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado con id: " + id));
        colegio.setActivo(true);
        Colegio colegioActualizado = colegioRepository.save(colegio);
        return convertirAResponseDTO(colegioActualizado);
    }

    public ColegioResponseDTO desactivar(Long id) {
        Colegio colegio = colegioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado con id: " + id));
        colegio.setActivo(false);
        Colegio colegioActualizado = colegioRepository.save(colegio);
        return convertirAResponseDTO(colegioActualizado);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id) {
        Colegio colegio = colegioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado con id: " + id));

        // Verificar si tiene estudiantes asociados
        Long totalEstudiantes = colegioRepository.contarEstudiantesPorColegio(id);
        if (totalEstudiantes > 0) {
            throw new RuntimeException("No se puede eliminar el colegio porque tiene " +
                    totalEstudiantes + " estudiantes asociados. Considere desactivarlo en su lugar.");
        }

        colegioRepository.deleteById(id);
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    public ColegioResponseDTO obtenerConEstadisticas(Long id) {
        Colegio colegio = colegioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado con id: " + id));

        ColegioResponseDTO response = convertirAResponseDTO(colegio);
        response.setTotalEstudiantes(colegioRepository.contarEstudiantesPorColegio(id));

        return response;
    }

    public Long contarEstudiantes(Long colegioId) {
        return colegioRepository.contarEstudiantesPorColegio(colegioId);
    }

    public Long contarEstudiantesActivos(Long colegioId) {
        return colegioRepository.contarEstudiantesActivosPorColegio(colegioId);
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private ColegioResponseDTO convertirAResponseDTO(Colegio colegio) {
        ColegioResponseDTO dto = new ColegioResponseDTO();
        dto.setId(colegio.getId());
        dto.setNombreColegio(colegio.getNombreColegio());
        dto.setActivo(colegio.getActivo());

        // Datos de zona (sin toda la entidad)
        if (colegio.getZona() != null) {
            dto.setZonaId(colegio.getZona().getId());
            dto.setNombreZona(colegio.getZona().getNombreZona());
        }

        // Jornadas del colegio (opcional - se llena bajo demanda)
        if (colegio.getColegioJornadas() != null && !colegio.getColegioJornadas().isEmpty()) {
            List<ColegioResponseDTO.JornadaInfo> jornadas = colegio.getColegioJornadas().stream()
                    .filter(cj -> cj.getActiva())
                    .map(cj -> {
                        ColegioResponseDTO.JornadaInfo info = new ColegioResponseDTO.JornadaInfo();
                        info.setId(cj.getJornada().getId());
                        info.setNombre(cj.getJornada().getNombreJornada().toString());
                        return info;
                    })
                    .collect(Collectors.toList());
            dto.setJornadas(jornadas);
        }

        return dto;
    }

    // Método interno para obtener entidad (usado por otros services)
    public Colegio obtenerEntidadPorId(Long id) {
        return colegioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado con id: " + id));
    }
}
