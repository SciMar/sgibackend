package com.sgi.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "asistencias",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fk_estudiante", "fecha", "tipo_recorrido"}))
@Data
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "fk_colegio", nullable = false)
    private Colegio colegio;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_registro", nullable = false)
    private LocalTime horaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recorrido", nullable = false, length = 20)
    private TipoRecorrido tipoRecorrido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_asistencia", nullable = false, length = 20)
    private EstadoAsistencia estadoAsistencia;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "fk_monitor", nullable = false)
    private Monitor monitor;

    @PrePersist
    protected void onCreate() {
        if (horaRegistro == null) {
            horaRegistro = LocalTime.now();
        }
        if (fecha == null) {
            fecha = LocalDate.now();
        }
    }
}
