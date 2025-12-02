package com.sgi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "monitores")
@Data
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "fk_usuario", unique = true, nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "fk_zona", nullable = false)
    private Zona zona;

    @ManyToOne
    @JoinColumn(name = "fk_jornada", nullable = false)
    private Jornada jornada;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(nullable = false)
    private Boolean activo = true;

    // Relación con Asistencias
    @OneToMany(mappedBy = "monitor")
    @JsonIgnore
    private List<Asistencia> asistencias;
}
