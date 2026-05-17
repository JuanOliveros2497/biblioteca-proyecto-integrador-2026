package com.biblioteca.service;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    public Prestamo crear(Prestamo prestamo) {
        Libro libro = prestamo.getLibro();
        if (!libro.isDisponible() || libro.getCantidadDisponible() <= 0) {
            throw new RuntimeException("El libro no está disponible");
        }
        libro.setCantidadDisponible(libro.getCantidadDisponible() - 1);
        if (libro.getCantidadDisponible() == 0) {
            libro.setDisponible(false);
        }
        libroRepository.save(libro);
        return prestamoRepository.save(prestamo);
    }

    public Prestamo devolver(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));
        prestamo.setEstado(Prestamo.Estado.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDateTime.now());
        Libro libro = prestamo.getLibro();
        libro.setCantidadDisponible(libro.getCantidadDisponible() + 1);
        libro.setDisponible(true);
        libroRepository.save(libro);
        return prestamoRepository.save(prestamo);
    }

    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAll();
    }

    public List<Prestamo> listarPorUsuario(Usuario usuario) {
        return prestamoRepository.findByUsuario(usuario);
    }

    public List<Prestamo> listarVencidos() {
        return prestamoRepository.findVencidos(LocalDateTime.now());
    }

    public Optional<Prestamo> buscarPorId(Long id) {
        return prestamoRepository.findById(id);
    }
}