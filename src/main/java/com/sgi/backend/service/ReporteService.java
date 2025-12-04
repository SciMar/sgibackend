package com.sgi.backend.service;

import com.sgi.backend.adapter.ReportService;
import com.sgi.backend.dto.asistencia.AsistenciaResponseDTO;
import com.sgi.backend.model.*;
import com.sgi.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.sgi.backend.external.PdfEstadisticoGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired
    @Qualifier("pdfReportAdapter")
    private ReportService pdfService;

    @Autowired
    @Qualifier("excelReportAdapter")
    private ReportService excelService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private ColegioRepository colegioRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private PdfEstadisticoGenerator pdfEstadisticoGenerator;

    // ==========================================
    // MÉTODOS GENÉRICOS DEL ADAPTER
    // ==========================================

    public byte[] generarReporte(String tipo, String reportName, List<String> headers, List<Map<String, Object>> data) {
        ReportService service = getServiceByType(tipo);
        return service.generateReport(reportName, headers, data);
    }

    public String getContentType(String tipo) {
        return getServiceByType(tipo).getContentType();
    }

    public String getFileExtension(String tipo) {
        return getServiceByType(tipo).getFileExtension();
    }

    private ReportService getServiceByType(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "pdf" -> pdfService;
            case "excel", "xlsx" -> excelService;
            default -> pdfService;
        };
    }

    // ==========================================
    // REPORTES ESPECÍFICOS
    // ==========================================

    /**
     * Reporte de Usuarios
     */
    public byte[] generarReporteUsuarios(String formato) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<String> headers = List.of(
                "ID", "Documento", "Nombre Completo", "Email", "Rol", "Estado"
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (Usuario user : usuarios) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", user.getId());
            row.put("Documento", user.getTipoId() + " " + user.getNumId());
            row.put("Nombre Completo", user.getPrimerNombre() + " " + user.getPrimerApellido());
            row.put("Email", user.getEmail());
            row.put("Rol", user.getRol().name());
            row.put("Estado", user.getActivo() ? "Activo" : "Inactivo");
            data.add(row);
        }

        return generarReporte(formato, "Reporte de Usuarios", headers, data);
    }

    /**
     * Reporte de Estudiantes
     */
    public byte[] generarReporteEstudiantes(String formato) {
        List<Estudiante> estudiantes = estudianteRepository.findAll();

        List<String> headers = List.of(
                "ID", "Documento", "Nombre Completo", "Curso", "Colegio", "Jornada",
                "Acudiente", "Teléfono", "Estado"
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (Estudiante est : estudiantes) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", est.getId());
            row.put("Documento", est.getTipoId() + " " + est.getNumId());
            row.put("Nombre Completo", est.getPrimerNombre() + " " + est.getPrimerApellido());
            row.put("Curso", est.getCurso());
            row.put("Colegio", est.getColegio() != null ? est.getColegio().getNombreColegio() : "N/A");
            row.put("Jornada", est.getJornada() != null ? est.getJornada().getNombreJornada().name() : "N/A");
            row.put("Acudiente", est.getNombreAcudiente());
            row.put("Teléfono", est.getTelefonoAcudiente());
            row.put("Estado", est.getActivo() ? "Activo" : "Inactivo");
            data.add(row);
        }

        return generarReporte(formato, "Reporte de Estudiantes", headers, data);
    }

    /**
     * Reporte de Zonas
     */
    public byte[] generarReporteZonas(String formato) {
        List<Zona> zonas = zonaRepository.findAll();

        List<String> headers = List.of(
                "ID", "Código", "Nombre", "Descripción", "Estado"
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (Zona zona : zonas) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", zona.getId());
            row.put("Código", zona.getCodigoZona());
            row.put("Nombre", zona.getNombreZona());
            row.put("Descripción", zona.getDescripcion());
            row.put("Estado", zona.getActiva() ? "Activa" : "Inactiva");
            data.add(row);
        }

        return generarReporte(formato, "Reporte de Zonas", headers, data);
    }

    /**
     * Reporte de Colegios
     */
    public byte[] generarReporteColegios(String formato) {
        List<Colegio> colegios = colegioRepository.findAll();

        List<String> headers = List.of(
                "ID", "Nombre", "Zona", "Estado"
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (Colegio colegio : colegios) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", colegio.getId());
            row.put("Nombre", colegio.getNombreColegio());
            row.put("Zona", colegio.getZona() != null ? colegio.getZona().getNombreZona() : "N/A");
            row.put("Estado", colegio.getActivo() ? "Activo" : "Inactivo");
            data.add(row);
        }

        return generarReporte(formato, "Reporte de Colegios", headers, data);
    }

    /**
     * Reporte de Rutas
     */
    public byte[] generarReporteRutas(String formato) {
        List<Ruta> rutas = rutaRepository.findAll();

        List<String> headers = List.of(
                "ID", "Nombre", "Tipo", "Zona", "Estado"
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (Ruta ruta : rutas) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", ruta.getId());
            row.put("Nombre", ruta.getNombreRuta());
            row.put("Tipo", ruta.getTipoRuta().name());
            row.put("Zona", ruta.getZona() != null ? ruta.getZona().getNombreZona() : "N/A");
            row.put("Estado", ruta.getActiva() ? "Activa" : "Inactiva");
            data.add(row);
        }

        return generarReporte(formato, "Reporte de Rutas", headers, data);
    }

    /**
     * Reporte de Jornadas
     */
    public byte[] generarReporteJornadas(String formato) {
        List<Jornada> jornadas = jornadaRepository.findAll();

        List<String> headers = List.of(
                "ID", "Código", "Tipo", "Zona", "Estado"
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (Jornada jornada : jornadas) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", jornada.getId());
            row.put("Código", jornada.getCodigoJornada());
            row.put("Tipo", jornada.getNombreJornada().name());
            row.put("Zona", jornada.getZona() != null ? jornada.getZona().getNombreZona() : "N/A");
            row.put("Estado", jornada.getActiva() ? "Activa" : "Inactiva");
            data.add(row);
        }

        return generarReporte(formato, "Reporte de Jornadas", headers, data);
    }

    /**
     * Reporte de Monitores
     */
    public byte[] generarReporteMonitores(String formato) {
        List<Monitor> monitores = monitorRepository.findAll();

        List<String> headers = List.of(
                "ID", "Nombre Monitor", "Email", "Zona", "Jornada", "Fecha Asignación", "Estado"
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (Monitor monitor : monitores) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", monitor.getId());
            row.put("Nombre Monitor", monitor.getUsuario() != null ?
                    monitor.getUsuario().getPrimerNombre() + " " + monitor.getUsuario().getPrimerApellido() : "N/A");
            row.put("Email", monitor.getUsuario() != null ? monitor.getUsuario().getEmail() : "N/A");
            row.put("Zona", monitor.getZona() != null ? monitor.getZona().getNombreZona() : "N/A");
            row.put("Jornada", monitor.getJornada() != null ? monitor.getJornada().getNombreJornada().name() : "N/A");
            row.put("Fecha Asignación", monitor.getFechaAsignacion() != null ? monitor.getFechaAsignacion().toString() : "N/A");
            row.put("Estado", monitor.getActivo() ? "Activo" : "Inactivo");
            data.add(row);
        }

        return generarReporte(formato, "Reporte de Monitores", headers, data);
    }

    /**
     * Reporte de Asistencias con filtros opcionales
     * Utiliza los métodos existentes del AsistenciaService
     */
    public byte[] generarReporteAsistencias(String formato, Long estudianteId, Long colegioId,
                                            Long monitorId, String fechaInicio, String fechaFin) {
        List<AsistenciaResponseDTO> asistencias;

        LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : null;
        LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : null;

        // Aplicar filtros según los parámetros recibidos
        if (estudianteId != null && inicio != null && fin != null) {
            asistencias = asistenciaService.listarPorEstudianteEnRango(estudianteId, inicio, fin);
        } else if (estudianteId != null) {
            asistencias = asistenciaService.listarPorEstudiante(estudianteId);
        } else if (colegioId != null && inicio != null && fin != null) {
            asistencias = asistenciaService.listarPorColegioEnRango(colegioId, inicio, fin);
        } else if (colegioId != null && inicio != null) {
            asistencias = asistenciaService.listarPorColegio(colegioId, inicio);
        } else if (monitorId != null && inicio != null && fin != null) {
            asistencias = asistenciaService.listarPorMonitorEnRango(monitorId, inicio, fin);
        } else if (monitorId != null && inicio != null) {
            asistencias = asistenciaService.listarPorMonitorYFecha(monitorId, inicio);
        } else if (monitorId != null) {
            asistencias = asistenciaService.listarPorMonitor(monitorId);
        } else if (inicio != null && fin != null) {
            asistencias = asistenciaService.listarPorRangoFechas(inicio, fin);
        } else if (inicio != null) {
            asistencias = asistenciaService.listarPorFecha(inicio);
        } else {
            asistencias = asistenciaService.listarDeHoy();
        }

        List<String> headers = List.of(
                "ID", "Fecha", "Hora", "Estudiante", "Documento", "Colegio",
                "Tipo Recorrido", "Estado", "Monitor", "Observaciones"
        );

        List<Map<String, Object>> data = new ArrayList<>();
        for (AsistenciaResponseDTO asistencia : asistencias) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", asistencia.getId());
            row.put("Fecha", asistencia.getFecha() != null ? asistencia.getFecha().toString() : "N/A");
            row.put("Hora", asistencia.getHoraRegistro() != null ? asistencia.getHoraRegistro().toString() : "N/A");
            row.put("Estudiante", asistencia.getNombreEstudiante() != null ? asistencia.getNombreEstudiante() : "N/A");
            row.put("Documento", asistencia.getNumIdEstudiante() != null ? asistencia.getNumIdEstudiante() : "N/A");
            row.put("Colegio", asistencia.getNombreColegio() != null ? asistencia.getNombreColegio() : "N/A");
            row.put("Tipo Recorrido", asistencia.getTipoRecorrido() != null ? asistencia.getTipoRecorrido().name() : "N/A");
            row.put("Estado", asistencia.getEstadoAsistencia() != null ? asistencia.getEstadoAsistencia().name() : "N/A");
            row.put("Monitor", asistencia.getNombreMonitor() != null ? asistencia.getNombreMonitor() : "N/A");
            row.put("Observaciones", asistencia.getObservaciones() != null ? asistencia.getObservaciones() : "");
            data.add(row);
        }

        String titulo = "Reporte de Asistencias";
        if (estudianteId != null) titulo += " - Estudiante";
        if (colegioId != null) titulo += " - Colegio";
        if (monitorId != null) titulo += " - Monitor";
        if (inicio != null && fin != null) titulo += " (" + inicio + " al " + fin + ")";

        return generarReporte(formato, titulo, headers, data);
    }

    /**
     * Reporte estadístico de un estudiante
     */
    public byte[] generarReporteEstadisticoEstudiante(Long estudianteId,
                                                      String fechaInicio,
                                                      String fechaFin) {
        Map<String, Object> estadisticas;

        if (fechaInicio != null && fechaFin != null) {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);
            estadisticas = asistenciaService.obtenerEstadisticasEstudianteEnRango(
                    estudianteId, inicio, fin
            );
        } else {
            estadisticas = asistenciaService.obtenerEstadisticasEstudiante(estudianteId);
        }

        return pdfEstadisticoGenerator.generarReporteEstadistico(
                "Estadísticas de Asistencia - Estudiante",
                estadisticas,
                "pie"
        );
    }

    /**
     * Reporte estadístico de un colegio
     */
    public byte[] generarReporteEstadisticoColegio(Long colegioId, String fecha) {
        LocalDate fechaConsulta = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
        Map<String, Long> estadisticas = asistenciaService.obtenerEstadisticasPorColegio(
                colegioId, fechaConsulta
        );

        // Convertir a Map<String, Object> para el generador
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.putAll(estadisticas);

        return pdfEstadisticoGenerator.generarReporteEstadistico(
                "Estadísticas de Asistencia - Colegio",
                stats,
                "barras"
        );
    }

    /**
     * Reporte estadístico general del día
     */
    public byte[] generarReporteEstadisticoGeneral(String fecha) {
        Map<String, Long> estadisticas;

        if (fecha != null) {
            LocalDate fechaConsulta = LocalDate.parse(fecha);
            estadisticas = asistenciaService.obtenerEstadisticasPorFecha(fechaConsulta);
        } else {
            estadisticas = asistenciaService.obtenerEstadisticasDeHoy();
        }

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.putAll(estadisticas);

        return pdfEstadisticoGenerator.generarReporteEstadistico(
                "Estadísticas Generales de Asistencia",
                stats,
                "pie"
        );
    }
}