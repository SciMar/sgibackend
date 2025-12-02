package com.sgi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "estudiantes")
@Data
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos del estudiante
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_id", length = 50)
    private TipoId tipoId;

    @Column(name = "num_id", unique = true, nullable = false, length = 50)
    private String numId;

    @Column(name = "primer_nombre", nullable = false, length = 100)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false, length = 100)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Sexo sexo;

    @Column(length = 200)
    private String direccion;

    @Column(length = 50)
    private String curso;

    @Column(length = 100)
    private String eps;

    @Column(length = 200)
    private String discapacidad;

    @Column(length = 100)
    private String etnia;

    // Datos del acudiente
    @Column(name = "nombre_acudiente", nullable = false, length = 300)
    private String nombreAcudiente;

    @Column(name = "telefono_acudiente", nullable = false, length = 20)
    private String telefonoAcudiente;

    @Column(name = "direccion_acudiente", length = 300)
    private String direccionAcudiente;

    @Column(name = "email_acudiente", length = 150)
    private String emailAcudiente;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "fk_colegio", nullable = false)
    private Colegio colegio;

    @ManyToOne
    @JoinColumn(name = "fk_jornada", nullable = false)
    private Jornada jornada;

    @ManyToOne
    @JoinColumn(name = "fk_ruta")
    private Ruta ruta;

    // Datos de inscripción
    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Column(name = "estado_inscripcion", nullable = false, length = 20)
    private String estadoInscripcion = "ACTIVA"; // ACTIVA, SUSPENDIDA, FINALIZADA

    @Column(name = "observaciones_inscripcion", length = 500)
    private String observacionesInscripcion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDate.now();
        }
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDate.now();
        }
    }

    // Relación con Asistencias
    @JsonIgnore
    @OneToMany(mappedBy = "estudiante")
    private List<Asistencia> asistencias;
}