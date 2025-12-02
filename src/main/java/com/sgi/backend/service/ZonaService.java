package com.sgi.backend.service;

import com.sgi.backend.dto.zona.CrearZonaDTO;
import com.sgi.backend.dto.zona.ActualizarZonaDTO;
import com.sgi.backend.dto.zona.ZonaResponseDTO;
import com.sgi.backend.model.Zona;
import com.sgi.backend.repository.ZonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ZonaService {

    @Autowired
    private ZonaRepository zonaRepository;

    // ==========================================
    // CREAR
    // ==========================================

    public ZonaResponseDTO crear(CrearZonaDTO dto) {
        // Validar que no exista el código
        if (zonaRepository.existsByCodigoZona(dto.getCodigoZona())) {
            throw new RuntimeException("Ya existe una zona con el código: " + dto.getCodigoZona());
        }

        // Validar que no exista el nombre
        if (zonaRepository.existsByNombreZona(dto.getNombreZona())) {
            throw new RuntimeException("Ya existe una zona con el nombre: " + dto.getNombreZona());
        }

        Zona zona = new Zona();
        zona.setCodigoZona(dto.getCodigoZona());
        zona.setNombreZona(dto.getNombreZona());
        zona.setDescripcion(dto.getDescripcion());
        zona.setActiva(true);

        Zona zonaSave = zonaRepository.save(zona);
        return convertirAResponseDTO(zonaSave);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    public ZonaResponseDTO actualizar(Long id, ActualizarZonaDTO dto) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));

        // Validar código si cambió
        if (!zona.getCodigoZona().equals(dto.getCodigoZona())) {
            if (zonaRepository.existsByCodigoZona(dto.getCodigoZona())) {
                throw new RuntimeException("Ya existe una zona con ese código");
            }
            zona.setCodigoZona(dto.getCodigoZona());
        }

        // Validar nombre si cambió
        if (!zona.getNombreZona().equals(dto.getNombreZona())) {
            if (zonaRepository.existsByNombreZona(dto.getNombreZona())) {
                throw new RuntimeException("Ya existe una zona con ese nombre");
            }
            zona.setNombreZona(dto.getNombreZona());
        }

        zona.setDescripcion(dto.getDescripcion());

        Zona zonaActualizada = zonaRepository.save(zona);
        return convertirAResponseDTO(zonaActualizada);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<ZonaResponseDTO> listarTodas() {
        return zonaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ZonaResponseDTO> listarActivas() {
        return zonaRepository.findByActivaTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public ZonaResponseDTO obtenerPorId(Long id) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));
        return convertirAResponseDTO(zona);
    }

    public ZonaResponseDTO obtenerPorCodigo(String codigoZona) {
        Zona zona = zonaRepository.findByCodigoZona(codigoZona)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con código: " + codigoZona));
        return convertirAResponseDTO(zona);
    }

    public List<ZonaResponseDTO> buscarPorNombre(String nombre) {
        return zonaRepository.findByNombreZonaContainingIgnoreCase(nombre).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    public ZonaResponseDTO activar(Long id) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));
        zona.setActiva(true);
        Zona zonaActualizada = zonaRepository.save(zona);
        return convertirAResponseDTO(zonaActualizada);
    }

    public ZonaResponseDTO desactivar(Long id) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));
        zona.setActiva(false);
        Zona zonaActualizada = zonaRepository.save(zona);
        return convertirAResponseDTO(zonaActualizada);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));

        // Verificar si tiene colegios asociados
        Long totalColegios = zonaRepository.contarColegiosPorZona(id);
        if (totalColegios > 0) {
            throw new RuntimeException("No se puede eliminar la zona porque tiene " +
                    totalColegios + " colegios asociados");
        }

        // Verificar si tiene monitores asociados
        Long totalMonitores = zonaRepository.contarMonitoresPorZona(id);
        if (totalMonitores > 0) {
            throw new RuntimeException("No se puede eliminar la zona porque tiene " +
                    totalMonitores + " monitores asociados");
        }

        zonaRepository.deleteById(id);
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    public ZonaResponseDTO obtenerConEstadisticas(Long id) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));

        ZonaResponseDTO response = convertirAResponseDTO(zona);
        response.setTotalColegios(zonaRepository.contarColegiosPorZona(id));
        response.setTotalMonitores(zonaRepository.contarMonitoresPorZona(id));
        response.setTotalJornadas(zonaRepository.contarJornadasPorZona(id));
        response.setTotalRutas(zonaRepository.contarRutasPorZona(id));

        return response;
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private ZonaResponseDTO convertirAResponseDTO(Zona zona) {
        ZonaResponseDTO dto = new ZonaResponseDTO();
        dto.setId(zona.getId());
        dto.setCodigoZona(zona.getCodigoZona());
        dto.setNombreZona(zona.getNombreZona());
        dto.setDescripcion(zona.getDescripcion());
        dto.setActiva(zona.getActiva());
        return dto;
    }

    // Método interno para obtener entidad (usado por otros services)
    public Zona obtenerEntidadPorId(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));
    }
}
