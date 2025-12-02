package com.sgi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "rutas")
@Data
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_ruta", nullable = false, length = 200)
    private String nombreRuta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ruta", nullable = false, length = 20)
    private TipoRecorrido tipoRuta;

    @ManyToOne
    @JoinColumn(name = "fk_zona", nullable = false)
    private Zona zona;

    @Column(nullable = false)
    private Boolean activa = true;

    // Relación con Estudiantes
    @JsonIgnore
    @OneToMany(mappedBy = "ruta")
    private List<Estudiante> estudiantes;
}
