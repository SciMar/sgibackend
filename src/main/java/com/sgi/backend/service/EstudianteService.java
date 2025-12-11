package com.sgi.backend.service;

import com.sgi.backend.dto.estudiante.CrearEstudianteDTO;
import com.sgi.backend.dto.estudiante.ActualizarEstudianteDTO;
import com.sgi.backend.dto.estudiante.EstudianteResponseDTO;
import com.sgi.backend.model.*;
import com.sgi.backend.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ColegioService colegioService;

    @Autowired
    private JornadaService jornadaService;

    @Autowired
    private RutaService rutaService;

    // ==========================================
    // CREAR
    // ==========================================

    public EstudianteResponseDTO crear(CrearEstudianteDTO dto) {
        // Validar que no exista el número de identificación
        if (estudianteRepository.existsByNumId(dto.getNumId())) {
            throw new RuntimeException("Ya existe un estudiante con el número de identificación: " + dto.getNumId());
        }

        // Obtener colegio y jornada
        Colegio colegio = colegioService.obtenerEntidadPorId(dto.getColegioId());
        Jornada jornada = jornadaService.obtenerEntidadPorId(dto.getJornadaId());

        // Validar que la jornada pertenezca a la misma zona del colegio
        if (!jornada.getZona().getId().equals(colegio.getZona().getId())) {
            throw new RuntimeException("La jornada seleccionada no pertenece a la zona del colegio");
        }

        Estudiante estudiante = new Estudiante();

        // Datos del estudiante
        estudiante.setTipoId(dto.getTipoId());
        estudiante.setNumId(dto.getNumId());
        estudiante.setPrimerNombre(dto.getPrimerNombre());
        estudiante.setSegundoNombre(dto.getSegundoNombre());
        estudiante.setPrimerApellido(dto.getPrimerApellido());
        estudiante.setSegundoApellido(dto.getSegundoApellido());
        estudiante.setFechaNacimiento(dto.getFechaNacimiento());
        estudiante.setSexo(dto.getSexo());
        estudiante.setDireccion(dto.getDireccion());
        estudiante.setCurso(dto.getCurso());
        estudiante.setEps(dto.getEps());
        estudiante.setDiscapacidad(dto.getDiscapacidad());
        estudiante.setEtnia(dto.getEtnia());


        // Datos del acudiente
        estudiante.setNombreAcudiente(dto.getNombreAcudiente());
        estudiante.setTelefonoAcudiente(dto.getTelefonoAcudiente());
        estudiante.setDireccionAcudiente(dto.getDireccionAcudiente());
        estudiante.setEmailAcudiente(dto.getEmailAcudiente());

        // Relaciones
        estudiante.setColegio(colegio);
        estudiante.setJornada(jornada);

        // Ruta (opcional)
        if (dto.getRutaId() != null) {
            Ruta ruta = rutaService.obtenerEntidadPorId(dto.getRutaId());

            // Validar que la ruta pertenezca a la misma zona
            if (!ruta.getZona().getId().equals(colegio.getZona().getId())) {
                throw new RuntimeException("La ruta seleccionada no pertenece a la zona del colegio");
            }

            estudiante.setRuta(ruta);
        }

        // Datos de inscripción
        estudiante.setFechaInscripcion(LocalDate.now());
        estudiante.setEstadoInscripcion("ACTIVA");
        estudiante.setObservacionesInscripcion(dto.getObservacionesInscripcion());
        estudiante.setFechaRegistro(LocalDate.now());
        estudiante.setActivo(true);

        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);
        return convertirAResponseDTO(estudianteGuardado);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================

    public EstudianteResponseDTO actualizar(Long id, ActualizarEstudianteDTO dto) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));

        // Validar numId si cambió
        if (!estudiante.getNumId().equals(dto.getNumId())) {
            if (estudianteRepository.existsByNumIdAndIdNot(dto.getNumId(), id)) {
                throw new RuntimeException("Ya existe otro estudiante con ese número de identificación");
            }
            estudiante.setNumId(dto.getNumId());
        }

        // Actualizar datos del estudiante
        estudiante.setTipoId(dto.getTipoId());
        estudiante.setPrimerNombre(dto.getPrimerNombre());
        estudiante.setSegundoNombre(dto.getSegundoNombre());
        estudiante.setPrimerApellido(dto.getPrimerApellido());
        estudiante.setSegundoApellido(dto.getSegundoApellido());
        estudiante.setFechaNacimiento(dto.getFechaNacimiento());
        estudiante.setSexo(dto.getSexo());
        estudiante.setDireccion(dto.getDireccion());
        estudiante.setCurso(dto.getCurso());
        estudiante.setEps(dto.getEps());
        estudiante.setDiscapacidad(dto.getDiscapacidad());
        estudiante.setEtnia(dto.getEtnia());

        // Actualizar datos del acudiente
        estudiante.setNombreAcudiente(dto.getNombreAcudiente());
        estudiante.setTelefonoAcudiente(dto.getTelefonoAcudiente());
        estudiante.setDireccionAcudiente(dto.getDireccionAcudiente());
        estudiante.setEmailAcudiente(dto.getEmailAcudiente());

        // Actualizar ruta si cambió
        if (dto.getRutaId() != null) {
            Ruta ruta = rutaService.obtenerEntidadPorId(dto.getRutaId());

            // Validar que la ruta pertenezca a la misma zona del colegio
            if (!ruta.getZona().getId().equals(estudiante.getColegio().getZona().getId())) {
                throw new RuntimeException("La ruta seleccionada no pertenece a la zona del colegio");
            }

            estudiante.setRuta(ruta);
        } else {
            estudiante.setRuta(null);
        }

        // ✅ Actualizar estado de inscripción si se envió
        if (dto.getEstadoInscripcion() != null) {
            estudiante.setEstadoInscripcion(dto.getEstadoInscripcion());
        }
        estudiante.setObservacionesInscripcion(dto.getObservacionesInscripcion());

        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);
        return convertirAResponseDTO(estudianteActualizado);
    }

    // ==========================================
    // LISTAR
    // ==========================================

    public List<EstudianteResponseDTO> listarTodos() {
        return estudianteRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarActivos() {
        return estudianteRepository.findByActivoTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarPorColegio(Long colegioId) {
        return estudianteRepository.findByColegioId(colegioId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarActivosPorColegio(Long colegioId) {
        return estudianteRepository.findByColegioIdAndActivoTrue(colegioId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarPorJornada(Long jornadaId) {
        return estudianteRepository.findByJornadaId(jornadaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarPorColegioYJornada(Long colegioId, Long jornadaId) {
        return estudianteRepository.findByColegioIdAndJornadaId(colegioId, jornadaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarActivosPorColegioYJornada(Long colegioId, Long jornadaId) {
        return estudianteRepository.findByColegioIdAndJornadaIdAndActivoTrue(colegioId, jornadaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarPorRuta(Long rutaId) {
        return estudianteRepository.findByRutaId(rutaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarPorZona(Long zonaId) {
        return estudianteRepository.findByZonaId(zonaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarPorEstadoInscripcion(String estadoInscripcion) {
        return estudianteRepository.findByEstadoInscripcion(estadoInscripcion).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR (PARA MONITORES - MUY IMPORTANTE)
    // ==========================================

    public List<EstudianteResponseDTO> listarEstudiantesParaMonitor(Long zonaId, Long jornadaId) {
        return estudianteRepository.findEstudiantesParaMonitor(zonaId, jornadaId).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarEstudiantesParaMonitorPorEstado(Long zonaId, Long jornadaId, String estado) {
        return estudianteRepository.findEstudiantesParaMonitorPorEstado(zonaId, jornadaId, estado).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // BUSCAR
    // ==========================================

    public EstudianteResponseDTO obtenerPorId(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));
        return convertirAResponseDTO(estudiante);
    }

    public EstudianteResponseDTO obtenerPorNumId(String numId) {
        Estudiante estudiante = estudianteRepository.findByNumId(numId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con número de identificación: " + numId));
        return convertirAResponseDTO(estudiante);
    }

    public List<EstudianteResponseDTO> buscarPorNombre(String nombre) {
        return estudianteRepository.buscarPorNombre(nombre).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // ACTIVAR / DESACTIVAR
    // ==========================================

    public EstudianteResponseDTO activar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));
        estudiante.setActivo(true);
        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);
        return convertirAResponseDTO(estudianteActualizado);
    }

    public EstudianteResponseDTO desactivar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));
        estudiante.setActivo(false);
        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);
        return convertirAResponseDTO(estudianteActualizado);
    }

    // ==========================================
    // CAMBIAR ESTADO DE INSCRIPCIÓN
    // ==========================================

    public EstudianteResponseDTO cambiarEstadoInscripcion(Long id, String nuevoEstado, String observaciones) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));

        // Validar estado
        if (!nuevoEstado.equals("ACTIVA") && !nuevoEstado.equals("SUSPENDIDA") && !nuevoEstado.equals("FINALIZADA")) {
            throw new RuntimeException("Estado de inscripción inválido. Debe ser: ACTIVA, SUSPENDIDA o FINALIZADA");
        }

        estudiante.setEstadoInscripcion(nuevoEstado);

        if (observaciones != null && !observaciones.isEmpty()) {
            estudiante.setObservacionesInscripcion(observaciones);
        }

        // Si se finaliza o suspende, desactivar el estudiante
        if (nuevoEstado.equals("SUSPENDIDA") || nuevoEstado.equals("FINALIZADA")) {
            estudiante.setActivo(false);
        }

        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);
        return convertirAResponseDTO(estudianteActualizado);
    }

    public EstudianteResponseDTO suspender(Long id, String motivo) {
        return cambiarEstadoInscripcion(id, "SUSPENDIDA", motivo);
    }

    public EstudianteResponseDTO finalizar(Long id, String motivo) {
        return cambiarEstadoInscripcion(id, "FINALIZADA", motivo);
    }

    public EstudianteResponseDTO reactivar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));

        estudiante.setEstadoInscripcion("ACTIVA");
        estudiante.setActivo(true);

        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);
        return convertirAResponseDTO(estudianteActualizado);
    }

    // ==========================================
    // CAMBIAR RUTA
    // ==========================================

    public EstudianteResponseDTO cambiarRuta(Long estudianteId, Long rutaId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + estudianteId));

        if (rutaId != null) {
            Ruta ruta = rutaService.obtenerEntidadPorId(rutaId);

            // Validar que la ruta pertenezca a la misma zona
            if (!ruta.getZona().getId().equals(estudiante.getColegio().getZona().getId())) {
                throw new RuntimeException("La ruta seleccionada no pertenece a la zona del estudiante");
            }

            estudiante.setRuta(ruta);
        } else {
            estudiante.setRuta(null);
        }

        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);
        return convertirAResponseDTO(estudianteActualizado);
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    public void eliminar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));

        // Verificar si tiene asistencias registradas
        Long totalAsistencias = estudianteRepository.contarAsistenciasPorEstudiante(id);
        if (totalAsistencias > 0) {
            throw new RuntimeException("No se puede eliminar el estudiante porque tiene " +
                    totalAsistencias + " asistencias registradas. Considere desactivarlo en su lugar.");
        }

        estudianteRepository.deleteById(id);
    }

    // ==========================================
    // ESTADÍSTICAS Y ASISTENCIAS
    // ==========================================

    public EstudianteResponseDTO obtenerConEstadisticas(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));

        EstudianteResponseDTO response = convertirAResponseDTO(estudiante);
        response.setTotalAsistencias(estudianteRepository.contarAsistenciasPorEstudiante(id));
        response.setPorcentajeAsistencia(estudianteRepository.calcularPorcentajeAsistencia(id));

        return response;
    }

    public Long contarAsistencias(Long estudianteId) {
        return estudianteRepository.contarAsistenciasPorEstudiante(estudianteId);
    }

    public Long contarAsistenciasPorEstado(Long estudianteId, String estado) {
        return estudianteRepository.contarAsistenciasPorEstadoYEstudiante(estudianteId, estado);
    }

    public Long contarAsistenciasEnRango(Long estudianteId, LocalDate fechaInicio, LocalDate fechaFin) {
        return estudianteRepository.contarAsistenciasPorEstudianteYRangoFechas(estudianteId, fechaInicio, fechaFin);
    }

    public Double calcularPorcentajeAsistencia(Long estudianteId) {
        Double porcentaje = estudianteRepository.calcularPorcentajeAsistencia(estudianteId);
        return porcentaje != null ? porcentaje : 0.0;
    }

    // ==========================================
    // REPORTES POR FECHAS
    // ==========================================

    public List<EstudianteResponseDTO> listarInscritosRecientes(int dias) {
        LocalDate fechaLimite = LocalDate.now().minusDays(dias);
        return estudianteRepository.findEstudiantesInscritosRecientes(fechaLimite).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> listarInscritosEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        return estudianteRepository.findByFechaInscripcionBetween(fechaInicio, fechaFin).stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // ==========================================
    // CONTADORES
    // ==========================================

    public Long contarPorColegio(Long colegioId) {
        return estudianteRepository.countByColegioId(colegioId);
    }

    public Long contarActivosPorColegio(Long colegioId) {
        return estudianteRepository.countByColegioAndActivoTrue(
                colegioService.obtenerEntidadPorId(colegioId)
        );
    }

    public Long contarPorRuta(Long rutaId) {
        return estudianteRepository.countByRutaId(rutaId);
    }

    public Long contarPorEstadoInscripcion(String estado) {
        return estudianteRepository.countByEstadoInscripcion(estado);
    }

    public Long contarEstudiantesDeMonitor(Long zonaId, Long jornadaId) {
        return estudianteRepository.contarEstudiantesDeMonitor(zonaId, jornadaId);
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private EstudianteResponseDTO convertirAResponseDTO(Estudiante estudiante) {
        EstudianteResponseDTO dto = new EstudianteResponseDTO();

        // Datos del estudiante
        dto.setId(estudiante.getId());
        dto.setTipoId(estudiante.getTipoId());
        dto.setNumId(estudiante.getNumId());
        dto.setPrimerNombre(estudiante.getPrimerNombre());
        dto.setSegundoNombre(estudiante.getSegundoNombre());
        dto.setPrimerApellido(estudiante.getPrimerApellido());
        dto.setSegundoApellido(estudiante.getSegundoApellido());

        // Nombre completo
        String nombreCompleto = estudiante.getPrimerNombre() + " " +
                (estudiante.getSegundoNombre() != null ? estudiante.getSegundoNombre() + " " : "") +
                estudiante.getPrimerApellido() + " " +
                (estudiante.getSegundoApellido() != null ? estudiante.getSegundoApellido() : "");
        dto.setNombreCompleto(nombreCompleto.trim());

        dto.setFechaNacimiento(estudiante.getFechaNacimiento());
        dto.setSexo(estudiante.getSexo());
        dto.setDireccion(estudiante.getDireccion());
        dto.setCurso(estudiante.getCurso());
        dto.setEps(estudiante.getEps());
        dto.setDiscapacidad(estudiante.getDiscapacidad());
        dto.setEtnia(estudiante.getEtnia());

        // Datos del acudiente
        dto.setNombreAcudiente(estudiante.getNombreAcudiente());
        dto.setTelefonoAcudiente(estudiante.getTelefonoAcudiente());
        dto.setDireccionAcudiente(estudiante.getDireccionAcudiente());
        dto.setEmailAcudiente(estudiante.getEmailAcudiente());

        // Datos de colegio
        if (estudiante.getColegio() != null) {
            dto.setColegioId(estudiante.getColegio().getId());
            dto.setNombreColegio(estudiante.getColegio().getNombreColegio());
        }

        // Datos de jornada
        if (estudiante.getJornada() != null) {
            dto.setJornadaId(estudiante.getJornada().getId());
            dto.setNombreJornada(estudiante.getJornada().getNombreJornada().getDisplayName());  // "Mañana"
            dto.setTipoJornada(estudiante.getJornada().getNombreJornada().name());              // "MANANA"
        }

        // Datos de ruta
        if (estudiante.getRuta() != null) {
            dto.setRutaId(estudiante.getRuta().getId());
            dto.setNombreRuta(estudiante.getRuta().getNombreRuta());
        }

        // Datos de inscripción
        dto.setFechaInscripcion(estudiante.getFechaInscripcion());
        dto.setEstadoInscripcion(estudiante.getEstadoInscripcion());
        dto.setObservacionesInscripcion(estudiante.getObservacionesInscripcion());
        dto.setFechaRegistro(estudiante.getFechaRegistro());
        dto.setActivo(estudiante.getActivo());

        return dto;
    }

    // Método interno para obtener entidad (usado por otros services)
    public Estudiante obtenerEntidadPorId(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con id: " + id));
    }
}
