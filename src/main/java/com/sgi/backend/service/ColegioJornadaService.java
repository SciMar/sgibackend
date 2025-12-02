package com.sgi.backend.service;

import com.sgi.backend.dto.colegiojornada.AsignarJornadaDTO;
import com.sgi.backend.dto.colegiojornada.ColegioJornadaResponseDTO;
import com.sgi.backend.model.ColegioJornada;
import com.sgi.backend.model.Colegio;
import com.sgi.backend.model.Jornada;
import com.sgi.backend.repository.ColegioJornadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ColegioJornadaService {

    @Autowired
    private ColegioJornadaRepository colegioJornadaRepository;

    @Autowired
    private ColegioService colegioService;

    @Autowired
    private JornadaService jornadaService;

    // ==========================================
    // ASIGNAR JORNADA A COLEGIO
    // ==========================================

    public ColegioJornadaResponseDTO asignar(AsignarJornadaDTO dto) {
        // Obtener colegio y jornada
        Colegio colegio = colegioService.obtenerEntidadPorId(dto.getColegioId());
        Jornada jornada = jornadaService.obtenerEntidadPorId(dto.getJornadaId());

        // Validar que la jornada pertenezca a la misma zona del colegio
        if (!jornada.getZona().getId().equals(colegio.getZona().getId())) {
            throw new RuntimeException("La jornada " + jornada.getNombreJornada() +
                    " no pertenece a la zona del colegio " + colegio.getNombreColegio());
        }

        // Validar que no exista ya la relación
        if (colegioJornadaRepository.existsByColegioIdAndJornadaId(dto.getColegioId(), dto.getJornadaId())) {
            throw new RuntimeException("La jornada " + jornada.getNombreJornada() +
                    " ya está asignada al colegio " + colegio.getNombreColegio());
        }

        ColegioJornada colegioJornada = new ColegioJornada();
        colegioJornada.setColegio(colegio);
        colegioJornada.setJornada(jornada);
        colegioJornada.setActiva(true);

        ColegioJornada colegioJornadaGuardada = colegioJornadaRepository.save(colegioJornada);
        return convertirAResponseDTO(colegioJornadaGuardada);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<ColegioJornadaResponseDTO> listarTodas() {
        return colegioJornadaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioJornadaResponseDTO> listarJornadasDeColegio(Long colegioId) {
        return colegioJornadaRepository.findByColegioId(colegioId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioJornadaResponseDTO> listarJornadasActivasDeColegio(Long colegioId) {
        return colegioJornadaRepository.findByColegioIdAndActivaTrue(colegioId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioJornadaResponseDTO> listarColegiosDeJornada(Long jornadaId) {
        return colegioJornadaRepository.findByJornadaId(jornadaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ColegioJornadaResponseDTO> listarColegiosActivosDeJornada(Long jornadaId) {
        return colegioJornadaRepository.findByJornadaAndActivaTrue(
                        jornadaService.obtenerEntidadPorId(jornadaId)
                ).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public ColegioJornadaResponseDTO obtenerPorId(Long id) {
        ColegioJornada colegioJornada = colegioJornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relación colegio-jornada no encontrada con id: " + id));
        return convertirAResponseDTO(colegioJornada);
    }

    public ColegioJornadaResponseDTO obtenerPorColegioYJornada(Long colegioId, Long jornadaId) {
        ColegioJornada colegioJornada = colegioJornadaRepository.findByColegioIdAndJornadaId(colegioId, jornadaId)
                .orElseThrow(() -> new RuntimeException("No se encontró relación entre el colegio y la jornada especificados"));
        return convertirAResponseDTO(colegioJornada);
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    public ColegioJornadaResponseDTO activar(Long id) {
        ColegioJornada colegioJornada = colegioJornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relación colegio-jornada no encontrada con id: " + id));
        colegioJornada.setActiva(true);
        ColegioJornada actualizada = colegioJornadaRepository.save(colegioJornada);
        return convertirAResponseDTO(actualizada);
    }

    public ColegioJornadaResponseDTO desactivar(Long id) {
        ColegioJornada colegioJornada = colegioJornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relación colegio-jornada no encontrada con id: " + id));
        colegioJornada.setActiva(false);
        ColegioJornada actualizada = colegioJornadaRepository.save(colegioJornada);
        return convertirAResponseDTO(actualizada);
    }

    public void activarPorColegioYJornada(Long colegioId, Long jornadaId) {
        colegioJornadaRepository.activarJornada(colegioId, jornadaId);
    }

    public void desactivarPorColegioYJornada(Long colegioId, Long jornadaId) {
        colegioJornadaRepository.desactivarJornada(colegioId, jornadaId);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id) {
        ColegioJornada colegioJornada = colegioJornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relación colegio-jornada no encontrada con id: " + id));

        // Verificar si hay estudiantes con esta combinación colegio-jornada
        // (esto se puede agregar si es necesario)

        colegioJornadaRepository.deleteById(id);
    }

    public void eliminarPorColegioYJornada(Long colegioId, Long jornadaId) {
        if (!colegioJornadaRepository.existsByColegioIdAndJornadaId(colegioId, jornadaId)) {
            throw new RuntimeException("No existe relación entre el colegio y la jornada especificados");
        }
        colegioJornadaRepository.deleteByColegioIdAndJornadaId(colegioId, jornadaId);
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    public boolean existeRelacion(Long colegioId, Long jornadaId) {
        return colegioJornadaRepository.existsByColegioIdAndJornadaId(colegioId, jornadaId);
    }

    public boolean existeRelacionActiva(Long colegioId, Long jornadaId) {
        Colegio colegio = colegioService.obtenerEntidadPorId(colegioId);
        Jornada jornada = jornadaService.obtenerEntidadPorId(jornadaId);
        return colegioJornadaRepository.existsByColegioAndJornadaAndActivaTrue(colegio, jornada);
    }

    // ==========================================
    // ESTADÍSTICAS
    // ==========================================

    public Long contarJornadasDeColegio(Long colegioId) {
        return colegioJornadaRepository.countByColegioId(colegioId);
    }

    public Long contarJornadasActivasDeColegio(Long colegioId) {
        return colegioJornadaRepository.countByColegioIdAndActivaTrue(colegioId);
    }

    // ==========================================
    // OPERACIONES MASIVAS
    // ==========================================

    public List<ColegioJornadaResponseDTO> asignarVariasJornadas(Long colegioId, List<Long> jornadaIds) {
        Colegio colegio = colegioService.obtenerEntidadPorId(colegioId);

        return jornadaIds.stream()
                .map(jornadaId -> {
                    // Validar que no exista ya
                    if (colegioJornadaRepository.existsByColegioIdAndJornadaId(colegioId, jornadaId)) {
                        return null; // Saltar si ya existe
                    }

                    Jornada jornada = jornadaService.obtenerEntidadPorId(jornadaId);

                    // Validar zona
                    if (!jornada.getZona().getId().equals(colegio.getZona().getId())) {
                        return null; // Saltar si no pertenece a la zona
                    }

                    ColegioJornada colegioJornada = new ColegioJornada();
                    colegioJornada.setColegio(colegio);
                    colegioJornada.setJornada(jornada);
                    colegioJornada.setActiva(true);

                    return colegioJornadaRepository.save(colegioJornada);
                })
                .filter(cj -> cj != null)
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public void eliminarTodasLasJornadasDeColegio(Long colegioId) {
        List<ColegioJornada> jornadas = colegioJornadaRepository.findByColegioId(colegioId);
        colegioJornadaRepository.deleteAll(jornadas);
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private ColegioJornadaResponseDTO convertirAResponseDTO(ColegioJornada colegioJornada) {
        ColegioJornadaResponseDTO dto = new ColegioJornadaResponseDTO();
        dto.setId(colegioJornada.getId());
        dto.setActiva(colegioJornada.getActiva());

        // Datos del colegio
        if (colegioJornada.getColegio() != null) {
            dto.setColegioId(colegioJornada.getColegio().getId());
            dto.setNombreColegio(colegioJornada.getColegio().getNombreColegio());
        }

        // Datos de la jornada
        if (colegioJornada.getJornada() != null) {
            dto.setJornadaId(colegioJornada.getJornada().getId());
            dto.setNombreJornada(colegioJornada.getJornada().getNombreJornada().toString());
        }

        return dto;
    }

    // Método interno para obtener entidad (usado por otros services)
    public ColegioJornada obtenerEntidadPorId(Long id) {
        return colegioJornadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relación colegio-jornada no encontrada con id: " + id));
    }
}
