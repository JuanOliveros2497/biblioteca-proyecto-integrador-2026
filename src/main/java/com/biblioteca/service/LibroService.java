package com.biblioteca.service;

import com.biblioteca.model.Libro;
import com.biblioteca.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    public Libro crear(Libro libro) {
        return libroRepository.save(libro);
    }

    public List<Libro> listarTodos() {
        return libroRepository.findAll();
    }

    public List<Libro> listarDisponibles() {
        return libroRepository.findByDisponibleTrue();
    }

    public Optional<Libro> buscarPorId(Long id) {
        return libroRepository.findById(id);
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Libro> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCase(autor);
    }

    public Libro actualizar(Libro libro) {
        return libroRepository.save(libro);
    }

    public void eliminar(Long id) {
        libroRepository.deleteById(id);
    }

    public List<Libro> buscar(String query) {
        if (query == null || query.isEmpty()) {
            return libroRepository.findAll();
        }
        List<Libro> porTitulo = libroRepository.findByTituloContainingIgnoreCase(query);
        List<Libro> porAutor = libroRepository.findByAutorContainingIgnoreCase(query);
        porTitulo.addAll(porAutor);
        return porTitulo.stream().distinct().toList();
    }
}