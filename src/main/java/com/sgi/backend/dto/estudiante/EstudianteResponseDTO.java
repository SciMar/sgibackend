package com.sgi.backend.dto.estudiante;

import com.sgi.backend.model.Sexo;
import com.sgi.backend.model.TipoId;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EstudianteResponseDTO {
    private Long id;

    // Datos del estudiante
    private TipoId tipoId;
    private String numId;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private Sexo sexo;
    private String direccion;
    private String curso;
    private String eps;
    private String discapacidad;
    private String etnia;

    // Datos del acudiente
    private String nombreAcudiente;
    private String telefonoAcudiente;
    private String direccionAcudiente;
    private String emailAcudiente;

    // Datos de colegio (sin toda la entidad)
    private Long colegioId;
    private String nombreColegio;

    // Datos de jornada
    private Long jornadaId;
    private String nombreJornada;
    private String tipoJornada;

    // Datos de ruta
    private Long rutaId;
    private String nombreRuta;

    // Datos de inscripción
    private LocalDate fechaInscripcion;
    private String estadoInscripcion;
    private String observacionesInscripcion;
    private LocalDate fechaRegistro;
    private Boolean activo;

    // Estadísticas opcionales
    private Long totalAsistencias;
    private Double porcentajeAsistencia;
}
