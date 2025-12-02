package com.sgi.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "colegio_jornada",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fk_colegio", "fk_jornada"}))
@Data
public class ColegioJornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_colegio", nullable = false)
    private Colegio colegio;

    @ManyToOne
    @JoinColumn(name = "fk_jornada", nullable = false)
    private Jornada jornada;

    @Column(nullable = false)
    private Boolean activa = true;
}
