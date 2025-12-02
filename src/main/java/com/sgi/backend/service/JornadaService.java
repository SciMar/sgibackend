package com.sgi.backend.service;

import com.sgi.backend.dto.jornada.CrearJornadaDTO;
import com.sgi.backend.dto.jornada.ActualizarJornadaDTO;
import com.sgi.backend.dto.jornada.JornadaResponseDTO;
import com.sgi.backend.model.Jornada;
import com.sgi.backend.model.Zona;
import com.sgi.backend.repository.JornadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JornadaService {

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private ZonaService zonaService;

    // ==========================================
    // CREAR
    // ==========================================

    public JornadaResponseDTO crear(CrearJornadaDTO dto) {
        // Validar que no exista el código
        if (jornadaRepository.existsByCodigoJornada(dto.getCodigoJornada())) {
            throw new RuntimeException("Ya existe una jornada con el código: " + dto.getCodigoJornada());
        }

        // Obtener la zona
        Zona zona = zonaService.obtenerEntidadPorId(dto.getZonaId());

        // Validar que no exista la combinación zona + tipo de jornada
        if (jornadaRepository.existsByZonaAndNombreJornada(zona, dto.getNombreJornada())) {
            throw new RuntimeException("Ya existe una jornada " + dto.getNombreJornada() +
                    " en la zona " + zona.getNombreZona());
        }

        Jornada jornada = new Jornada();
        jornada.setCodigoJornada(dto.getCodigoJornada());
        jornada.setNombreJornada(dto.getNombreJornada());
        jornada.setZona(zona);
        jornada.setActiva(true);

        Jornada jornadaGuardada = jornadaRepository.save(jornada);
        return convertirAResponseDTO(jornadaGuardada);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    public JornadaResponseDTO actualizar(Long id, ActualizarJornadaDTO dto) {
        Jornada jornada = jornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con id: " + id));

        // Validar código si cambió
        if (!jornada.getCodigoJornada().equals(dto.getCodigoJornada())) {
            if (jornadaRepository.existsByCodigoJornada(dto.getCodigoJornada())) {
                throw new RuntimeException("Ya existe una jornada con ese código");
            }
            jornada.setCodigoJornada(dto.getCodigoJornada());
        }

        jornada.setNombreJornada(dto.getNombreJornada());

        Jornada jornadaActualizada = jornadaRepository.save(jornada);
        return convertirAResponseDTO(jornadaActualizada);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<JornadaResponseDTO> listarTodas() {
        return jornadaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<JornadaResponseDTO> listarActivas() {
        return jornadaRepository.findByActivaTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<JornadaResponseDTO> listarPorZona(Long zonaId) {
        return jornadaRepository.findByZonaId(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<JornadaResponseDTO> listarActivasPorZona(Long zonaId) {
        return jornadaRepository.findByZonaIdAndActivaTrue(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public JornadaResponseDTO obtenerPorId(Long id) {
        Jornada jornada = jornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con id: " + id));
        return convertirAResponseDTO(jornada);
    }

    public JornadaResponseDTO obtenerPorCodigo(String codigoJornada) {
        Jornada jornada = jornadaRepository.findByCodigoJornada(codigoJornada)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con código: " + codigoJornada));
        return convertirAResponseDTO(jornada);
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    public JornadaResponseDTO activar(Long id) {
        Jornada jornada = jornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con id: " + id));
        jornada.setActiva(true);
        Jornada jornadaActualizada = jornadaRepository.save(jornada);
        return convertirAResponseDTO(jornadaActualizada);
    }

    public JornadaResponseDTO desactivar(Long id) {
        Jornada jornada = jornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con id: " + id));
        jornada.setActiva(false);
        Jornada jornadaActualizada = jornadaRepository.save(jornada);
        return convertirAResponseDTO(jornadaActualizada);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id) {
        Jornada jornada = jornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con id: " + id));

        // Verificar si tiene estudiantes asociados
        Long totalEstudiantes = jornadaRepository.contarEstudiantesPorJornada(id);
        if (totalEstudiantes > 0) {
            throw new RuntimeException("No se puede eliminar la jornada porque tiene " +
                    totalEstudiantes + " estudiantes asociados");
        }

        // Verificar si tiene monitores asociados
        Long totalMonitores = jornadaRepository.contarMonitoresPorJornada(id);
        if (totalMonitores > 0) {
            throw new RuntimeException("No se puede eliminar la jornada porque tiene " +
                    totalMonitores + " monitores asociados");
        }

        jornadaRepository.deleteById(id);
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    public JornadaResponseDTO obtenerConEstadisticas(Long id) {
        Jornada jornada = jornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con id: " + id));

        JornadaResponseDTO response = convertirAResponseDTO(jornada);
        response.setTotalEstudiantes(jornadaRepository.contarEstudiantesPorJornada(id));
        response.setTotalMonitores(jornadaRepository.contarMonitoresPorJornada(id));

        return response;
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private JornadaResponseDTO convertirAResponseDTO(Jornada jornada) {
        JornadaResponseDTO dto = new JornadaResponseDTO();
        dto.setId(jornada.getId());
        dto.setCodigoJornada(jornada.getCodigoJornada());
        dto.setNombreJornada(jornada.getNombreJornada());
        dto.setActiva(jornada.getActiva());

        // Datos de la zona
        if (jornada.getZona() != null) {
            dto.setZonaId(jornada.getZona().getId());
            dto.setNombreZona(jornada.getZona().getNombreZona());
        }

        return dto;
    }

    // Método interno para obtener entidad (usado por otros services)
    public Jornada obtenerEntidadPorId(Long id) {
        return jornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jornada no encontrada con id: " + id));
    }
}
