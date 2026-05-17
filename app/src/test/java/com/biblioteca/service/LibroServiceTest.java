package com.biblioteca.service;

import com.biblioteca.model.Categoria;
import com.biblioteca.model.Libro;
import com.biblioteca.repository.LibroRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    private Libro libro;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Ficción");

        libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Cien años de soledad");
        libro.setAutor("Gabriel García Márquez");
        libro.setIsbn("978-0307474728");
        libro.setCantidad(5);
        libro.setCantidadDisponible(5);
        libro.setDisponible(true);
        libro.setCategoria(categoria);
    }

    @Test
    void crear_DebeGuardarLibro() {
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        Libro resultado = libroService.crear(libro);

        assertNotNull(resultado);
        assertEquals("Cien años de soledad", resultado.getTitulo());
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    void crear_DebeIgualarCantidadDisponibleACantidad() {
        libro.setCantidad(10);
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        libroService.crear(libro);

        assertEquals(10, libro.getCantidadDisponible());
    }

    @Test
    void listarTodos_DebeRetornarTodosLosLibros() {
        when(libroRepository.findAll()).thenReturn(Arrays.asList(libro));

        List<Libro> resultado = libroService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void listarDisponibles_DebeRetornarSoloDisponibles() {
        when(libroRepository.findByDisponibleTrue()).thenReturn(Arrays.asList(libro));

        List<Libro> resultado = libroService.listarDisponibles();

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).isDisponible());
    }

    @Test
    void buscarPorId_ConIdExistente_DebeRetornarLibro() {
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        Optional<Libro> resultado = libroService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Cien años de soledad", resultado.get().getTitulo());
    }

    @Test
    void buscarPorId_ConIdInexistente_DebeRetornarVacio() {
        when(libroRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Libro> resultado = libroService.buscarPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void buscarPorTitulo_DebeRetornarLibrosCoincidentes() {
        when(libroRepository.findByTituloContainingIgnoreCase("soledad"))
                .thenReturn(Arrays.asList(libro));

        List<Libro> resultado = libroService.buscarPorTitulo("soledad");

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorAutor_DebeRetornarLibrosCoincidentes() {
        when(libroRepository.findByAutorContainingIgnoreCase("García"))
                .thenReturn(Arrays.asList(libro));

        List<Libro> resultado = libroService.buscarPorAutor("García");

        assertFalse(resultado.isEmpty());
    }

    @Test
    void buscar_ConQueryVacia_DebeRetornarTodos() {
        when(libroRepository.findAll()).thenReturn(Arrays.asList(libro));

        List<Libro> resultado = libroService.buscar("");

        assertFalse(resultado.isEmpty());
    }

    @Test
    void buscar_ConQuery_DebeRetornarCoincidentes() {
        when(libroRepository.findByTituloContainingIgnoreCase("soledad"))
                .thenReturn(Arrays.asList(libro));
        when(libroRepository.findByAutorContainingIgnoreCase("soledad"))
                .thenReturn(Arrays.asList());

        List<Libro> resultado = libroService.buscar("soledad");

        assertFalse(resultado.isEmpty());
    }

    @Test
    void eliminar_DebeEliminarLibro() {
        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        doNothing().when(libroRepository).delete(any(Libro.class));

        libroService.eliminar(1L);

        verify(libroRepository, times(1)).delete(any(Libro.class));
    }

    @Test
    void eliminar_ConIdInexistente_DebeLanzarExcepcion() {
        when(libroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> libroService.eliminar(99L));
    }
}