package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private boolean activo = true;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public enum Rol {
        ADMIN, USER
    }
}