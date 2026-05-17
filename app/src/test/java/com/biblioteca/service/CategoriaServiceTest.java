package com.biblioteca.service;

import com.biblioteca.model.Categoria;
import com.biblioteca.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Ficción");
        categoria.setDescripcion("Libros de ficción");
        categoria.setActiva(true);
    }

    @Test
    void crear_ConNombreNuevo_DebeGuardarCategoria() {
        when(categoriaRepository.existsByNombre("Ficción")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria resultado = categoriaService.crear(categoria);

        assertNotNull(resultado);
        assertEquals("Ficción", resultado.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void crear_ConNombreDuplicado_DebeLanzarExcepcion() {
        when(categoriaRepository.existsByNombre("Ficción")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoriaService.crear(categoria));

        assertEquals("Ya existe una categoría con ese nombre", ex.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void listarTodas_DebeRetornarTodasLasCategorias() {
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria));

        List<Categoria> resultado = categoriaService.listarTodas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_ConIdExistente_DebeRetornarCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Optional<Categoria> resultado = categoriaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Ficción", resultado.get().getNombre());
    }

    @Test
    void buscarPorId_ConIdInexistente_DebeRetornarVacio() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Categoria> resultado = categoriaService.buscarPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void actualizar_DebeGuardarCambios() {
        categoria.setNombre("Ciencia Ficción");
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria resultado = categoriaService.actualizar(categoria);

        assertEquals("Ciencia Ficción", resultado.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void eliminar_DebeEliminarCategoria() {
        doNothing().when(categoriaRepository).deleteById(1L);

        categoriaService.eliminar(1L);

        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}