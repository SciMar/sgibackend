package com.sgi.backend.controller;

import com.sgi.backend.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/usuarios/{formato}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<byte[]> reporteUsuarios(@PathVariable String formato) {
        try {
            byte[] reporte = reporteService.generarReporteUsuarios(formato);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=usuarios" + reporteService.getFileExtension(formato));
            headers.add("Content-Type", reporteService.getContentType(formato));

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estudiantes/{formato}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<byte[]> reporteEstudiantes(@PathVariable String formato) {
        try {
            byte[] reporte = reporteService.generarReporteEstudiantes(formato);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=estudiantes" + reporteService.getFileExtension(formato));
            headers.add("Content-Type", reporteService.getContentType(formato));

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/zonas/{formato}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<byte[]> reporteZonas(@PathVariable String formato) {
        try {
            byte[] reporte = reporteService.generarReporteZonas(formato);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=zonas" + reporteService.getFileExtension(formato));
            headers.add("Content-Type", reporteService.getContentType(formato));

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/colegios/{formato}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<byte[]> reporteColegios(@PathVariable String formato) {
        try {
            byte[] reporte = reporteService.generarReporteColegios(formato);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=colegios" + reporteService.getFileExtension(formato));
            headers.add("Content-Type", reporteService.getContentType(formato));

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rutas/{formato}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<byte[]> reporteRutas(@PathVariable String formato) {
        try {
            byte[] reporte = reporteService.generarReporteRutas(formato);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=rutas" + reporteService.getFileExtension(formato));
            headers.add("Content-Type", reporteService.getContentType(formato));

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/jornadas/{formato}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<byte[]> reporteJornadas(@PathVariable String formato) {
        try {
            byte[] reporte = reporteService.generarReporteJornadas(formato);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=jornadas" + reporteService.getFileExtension(formato));
            headers.add("Content-Type", reporteService.getContentType(formato));

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/monitores/{formato}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<byte[]> reporteMonitores(@PathVariable String formato) {
        try {
            byte[] reporte = reporteService.generarReporteMonitores(formato);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=monitores" + reporteService.getFileExtension(formato));
            headers.add("Content-Type", reporteService.getContentType(formato));

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/asistencias/{formato}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO', 'MONITOR')")
    public ResponseEntity<byte[]> reporteAsistencias(
            @PathVariable String formato,
            @RequestParam(required = false) Long estudianteId,
            @RequestParam(required = false) Long colegioId,
            @RequestParam(required = false) Long monitorId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            byte[] reporte = reporteService.generarReporteAsistencias(
                    formato, estudianteId, colegioId, monitorId, fechaInicio, fechaFin
            );

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=asistencias" + reporteService.getFileExtension(formato));
            headers.add("Content-Type", reporteService.getContentType(formato));

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estadisticas/estudiante/{estudianteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO', 'MONITOR')")
    public ResponseEntity<byte[]> reporteEstadisticoEstudiante(
            @PathVariable Long estudianteId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            byte[] reporte = reporteService.generarReporteEstadisticoEstudiante(
                    estudianteId, fechaInicio, fechaFin
            );

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=estadisticas-estudiante.pdf");
            headers.add("Content-Type", "application/pdf");

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estadisticas/colegio/{colegioId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<byte[]> reporteEstadisticoColegio(
            @PathVariable Long colegioId,
            @RequestParam(required = false) String fecha) {
        try {
            byte[] reporte = reporteService.generarReporteEstadisticoColegio(colegioId, fecha);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=estadisticas-colegio.pdf");
            headers.add("Content-Type", "application/pdf");

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estadisticas/general")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO')")
    public ResponseEntity<byte[]> reporteEstadisticoGeneral(
            @RequestParam(required = false) String fecha) {
        try {
            byte[] reporte = reporteService.generarReporteEstadisticoGeneral(fecha);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=estadisticas-general.pdf");
            headers.add("Content-Type", "application/pdf");

            return new ResponseEntity<>(reporte, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}