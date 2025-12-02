package com.sgi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "jornadas")
@Data
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cod_jornada", unique = true, nullable = false, length = 50)
    private String codigoJornada;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_jornada", nullable = false, length = 100)
    private TipoJornada nombreJornada;

    @ManyToOne
    @JoinColumn(name = "fk_zona", nullable = false)
    private Zona zona;

    @Column(nullable = false)
    private Boolean activa = true;

    // Relaciones
    @OneToMany(mappedBy = "jornada")
    private List<Estudiante> estudiantes;

    @OneToMany(mappedBy = "jornada")
    private List<Monitor> monitores;

    @OneToMany(mappedBy = "jornada")
    @JsonIgnore
    private List<ColegioJornada> colegioJornadas;
}
