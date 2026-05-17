package com.biblioteca;

import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("admin@biblioteca.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("admin@biblioteca.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(Usuario.Rol.ADMIN);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario admin creado: admin@biblioteca.com / admin123");
        }
    }
}