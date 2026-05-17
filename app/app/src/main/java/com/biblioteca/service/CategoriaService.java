package com.biblioteca.service;

import com.biblioteca.model.Categoria;
import com.biblioteca.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Categoria crear(Categoria categoria) {
        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria actualizar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }
}