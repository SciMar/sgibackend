package com.sgi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "zonas")
@Data
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cod_zona", unique = true, nullable = false, length = 50)
    private String codigoZona;

    @Column(name = "nombre_zona", nullable = false, length = 200)
    private String nombreZona;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activa = true;

    // Relaciones
    @JsonIgnore
    @OneToMany(mappedBy = "zona")
    private List<Jornada> jornadas;

    @JsonIgnore
    @OneToMany(mappedBy = "zona")
    private List<Colegio> colegios;

    @JsonIgnore
    @OneToMany(mappedBy = "zona")
    private List<Ruta> rutas;

    @JsonIgnore
    @OneToMany(mappedBy = "zona")
    private List<Monitor> monitores;
}