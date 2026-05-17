package com.biblioteca.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDateTime fechaPrestamo;

    @Column(name = "fecha_devolucion_esperada", nullable = false)
    private LocalDateTime fechaDevolucionEsperada;

    @Column(name = "fecha_devolucion_real")
    private LocalDateTime fechaDevolucionReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    private String observaciones;

    @PrePersist
    public void prePersist() {
        this.fechaPrestamo = LocalDateTime.now();
        this.fechaDevolucionEsperada = LocalDateTime.now().plusDays(15);
        this.estado = Estado.ACTIVO;
    }

    public boolean isVencido() {
        return estado == Estado.ACTIVO &&
               LocalDateTime.now().isAfter(fechaDevolucionEsperada);
    }

    public enum Estado {
        ACTIVO, DEVUELTO, VENCIDO
    }
}