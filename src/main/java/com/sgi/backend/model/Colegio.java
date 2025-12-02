package com.sgi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "colegios")
@Data
public class Colegio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_colegio", nullable = false, length = 200)
    private String nombreColegio;

    @ManyToOne
    @JoinColumn(name = "fk_zona", nullable = false)
    private Zona zona;

    @Column(nullable = false)
    private Boolean activo = true;

    // Relaciones
    @OneToMany(mappedBy = "colegio")
    @JsonIgnore
    private List<Estudiante> estudiantes;

    @OneToMany(mappedBy = "colegio", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ColegioJornada> colegioJornadas;

    @OneToMany(mappedBy = "colegio")
    @JsonIgnore
    private List<Asistencia> asistencias;
}
