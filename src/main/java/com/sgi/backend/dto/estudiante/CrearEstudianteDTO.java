package com.sgi.backend.dto.estudiante;

import com.sgi.backend.model.Sexo;
import com.sgi.backend.model.TipoId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CrearEstudianteDTO {

    // Datos del estudiante
    private TipoId tipoId;

    @NotBlank(message = "El número de identificación es obligatorio")
    @Size(max = 50)
    private String numId;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 100)
    private String primerNombre;

    @Size(max = 100)
    private String segundoNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 100)
    private String primerApellido;

    @Size(max = 100)
    private String segundoApellido;

    private LocalDate fechaNacimiento;

    private Sexo sexo;

    @Size(max = 200)
    private String direccion;

    @Size(max = 50)
    private String curso;

    @Size(max = 100)
    private String eps;

    @Size(max = 200)
    private String discapacidad;

    @Size(max = 100)
    private String etnia;

    // Datos del acudiente
    @NotBlank(message = "El nombre del acudiente es obligatorio")
    @Size(max = 300)
    private String nombreAcudiente;

    @NotBlank(message = "El teléfono del acudiente es obligatorio")
    @Size(max = 20)
    private String telefonoAcudiente;

    @Size(max = 300)
    private String direccionAcudiente;

    @Email(message = "Debe ser un email válido")
    @Size(max = 150)
    private String emailAcudiente;

    // Relaciones
    @NotNull(message = "El colegio es obligatorio")
    private Long colegioId;

    @NotNull(message = "La jornada es obligatoria")
    private Long jornadaId;

    private Long rutaId;

    // Datos de inscripción
    @Size(max = 500)
    private String observacionesInscripcion;
}