package com.biblioteca.service;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private PrestamoService prestamoService;

    private Usuario usuario;
    private Libro libroDisponible;
    private Libro libroSinStock;
    private Prestamo prestamoActivo;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Carlos Pérez");
        usuario.setEmail("carlos@biblioteca.com");

        libroDisponible = new Libro();
        libroDisponible.setId(10L);
        libroDisponible.setTitulo("Clean Code");
        libroDisponible.setDisponible(true);
        libroDisponible.setCantidadDisponible(3);

        libroSinStock = new Libro();
        libroSinStock.setId(11L);
        libroSinStock.setTitulo("The Pragmatic Programmer");
        libroSinStock.setDisponible(false);
        libroSinStock.setCantidadDisponible(0);

        prestamoActivo = new Prestamo();
        prestamoActivo.setId(100L);
        prestamoActivo.setUsuario(usuario);
        prestamoActivo.setLibro(libroDisponible);
        prestamoActivo.setEstado(Prestamo.Estado.ACTIVO);
        prestamoActivo.setFechaPrestamo(LocalDateTime.now().minusDays(5));
        prestamoActivo.setFechaDevolucionEsperada(LocalDateTime.now().plusDays(10));
    }

    // =========================================================
    // SUITE 1: crear() — casos exitosos
    // =========================================================

    @Test
    void crear_ConLibroDisponible_DebeGuardarPrestamoYReducirStock() {
        when(libroRepository.save(any(Libro.class))).thenReturn(libroDisponible);
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoActivo);

        Prestamo resultado = prestamoService.crear(prestamoActivo);

        assertNotNull(resultado);
        assertEquals(Prestamo.Estado.ACTIVO, resultado.getEstado());
        assertEquals(2, libroDisponible.getCantidadDisponible());
        verify(libroRepository, times(1)).save(libroDisponible);
        verify(prestamoRepository, times(1)).save(prestamoActivo);
    }

    @Test
    void crear_ConUltimoEjemplar_DebeMarcarLibroComoNoDisponible() {
        libroDisponible.setCantidadDisponible(1);
        when(libroRepository.save(any(Libro.class))).thenReturn(libroDisponible);
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoActivo);

        prestamoService.crear(prestamoActivo);

        assertEquals(0, libroDisponible.getCantidadDisponible());
        assertFalse(libroDisponible.isDisponible());
        verify(libroRepository, times(1)).save(libroDisponible);
    }

    @Test
    void crear_ConVariosEjemplares_LibroSigueSiendoDisponible() {
        when(libroRepository.save(any(Libro.class))).thenReturn(libroDisponible);
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoActivo);

        prestamoService.crear(prestamoActivo);

        assertEquals(2, libroDisponible.getCantidadDisponible());
        assertTrue(libroDisponible.isDisponible());
        verify(libroRepository, times(1)).save(libroDisponible);
    }

    // =========================================================
    // SUITE 2: crear() — casos de error
    // =========================================================

    @Test
    void crear_ConLibroNoDisponible_DebeLanzarExcepcion() {
        prestamoActivo.setLibro(libroSinStock);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> prestamoService.crear(prestamoActivo));

        assertEquals("El libro no está disponible", ex.getMessage());
        verify(libroRepository, never()).save(any());
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    void crear_ConCantidadCeroAunqueDisponibleTrue_DebeLanzarExcepcion() {
        libroSinStock.setDisponible(true);
        libroSinStock.setCantidadDisponible(0);
        prestamoActivo.setLibro(libroSinStock);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> prestamoService.crear(prestamoActivo));

        assertEquals("El libro no está disponible", ex.getMessage());
        verify(prestamoRepository, never()).save(any());
    }

    // =========================================================
    // SUITE 3: devolver()
    // =========================================================

    @Test
    void devolver_ConPrestamoActivo_DebeActualizarEstadoYAumentarStock() {
        int stockAntes = libroDisponible.getCantidadDisponible();
        when(prestamoRepository.findById(100L)).thenReturn(Optional.of(prestamoActivo));
        when(libroRepository.save(any(Libro.class))).thenReturn(libroDisponible);
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoActivo);

        Prestamo resultado = prestamoService.devolver(100L);

        assertEquals(Prestamo.Estado.DEVUELTO, resultado.getEstado());
        assertNotNull(resultado.getFechaDevolucionReal());
        assertEquals(stockAntes + 1, libroDisponible.getCantidadDisponible());
        assertTrue(libroDisponible.isDisponible());
        verify(prestamoRepository, times(1)).findById(100L);
        verify(libroRepository, times(1)).save(libroDisponible);
        verify(prestamoRepository, times(1)).save(prestamoActivo);
    }

    @Test
    void devolver_DebeRegistrarFechaDevolucionReal() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        when(prestamoRepository.findById(100L)).thenReturn(Optional.of(prestamoActivo));
        when(libroRepository.save(any(Libro.class))).thenReturn(libroDisponible);
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoActivo);

        prestamoService.devolver(100L);

        assertNotNull(prestamoActivo.getFechaDevolucionReal());
        assertTrue(prestamoActivo.getFechaDevolucionReal().isAfter(antes));
    }

    @Test
    void devolver_ConIdInexistente_DebeLanzarExcepcion() {
        when(prestamoRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> prestamoService.devolver(999L));

        assertEquals("Préstamo no encontrado", ex.getMessage());
        verify(libroRepository, never()).save(any());
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    void devolver_DebeMarcarLibroComoDisponibleNuevamente() {
        libroDisponible.setCantidadDisponible(0);
        libroDisponible.setDisponible(false);
        when(prestamoRepository.findById(100L)).thenReturn(Optional.of(prestamoActivo));
        when(libroRepository.save(any(Libro.class))).thenReturn(libroDisponible);
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamoActivo);

        prestamoService.devolver(100L);

        assertEquals(1, libroDisponible.getCantidadDisponible());
        assertTrue(libroDisponible.isDisponible());
    }

    // =========================================================
    // SUITE 4: listarTodos()
    // =========================================================

    @Test
    void listarTodos_DebeRetornarTodosLosPrestamos() {
        when(prestamoRepository.findAll()).thenReturn(Arrays.asList(prestamoActivo));

        List<Prestamo> resultado = prestamoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(prestamoRepository, times(1)).findAll();
    }

    @Test
    void listarTodos_SinPrestamos_DebeRetornarListaVacia() {
        when(prestamoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Prestamo> resultado = prestamoService.listarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(prestamoRepository, times(1)).findAll();
    }

    // =========================================================
    // SUITE 5: listarPorUsuario()
    // =========================================================

    @Test
    void listarPorUsuario_UsuarioConPrestamos_DebeRetornarSuLista() {
        when(prestamoRepository.findByUsuario(usuario))
                .thenReturn(Arrays.asList(prestamoActivo));

        List<Prestamo> resultado = prestamoService.listarPorUsuario(usuario);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuario, resultado.get(0).getUsuario());
        verify(prestamoRepository, times(1)).findByUsuario(usuario);
    }

    @Test
    void listarPorUsuario_UsuarioSinPrestamos_DebeRetornarListaVacia() {
        when(prestamoRepository.findByUsuario(usuario))
                .thenReturn(Collections.emptyList());

        List<Prestamo> resultado = prestamoService.listarPorUsuario(usuario);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(prestamoRepository, times(1)).findByUsuario(usuario);
    }

    // =========================================================
    // SUITE 6: listarVencidos()
    // =========================================================

    @Test
    void listarVencidos_HayPrestamosVencidos_DebeRetornarLista() {
        Prestamo vencido = new Prestamo();
        vencido.setId(200L);
        vencido.setEstado(Prestamo.Estado.ACTIVO);
        vencido.setFechaDevolucionEsperada(LocalDateTime.now().minusDays(3));
        vencido.setLibro(libroDisponible);
        vencido.setUsuario(usuario);

        when(prestamoRepository.findVencidos(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(vencido));

        List<Prestamo> resultado = prestamoService.listarVencidos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getFechaDevolucionEsperada()
                .isBefore(LocalDateTime.now()));
        verify(prestamoRepository, times(1)).findVencidos(any(LocalDateTime.class));
    }

    @Test
    void listarVencidos_SinVencidos_DebeRetornarListaVacia() {
        when(prestamoRepository.findVencidos(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<Prestamo> resultado = prestamoService.listarVencidos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(prestamoRepository, times(1)).findVencidos(any(LocalDateTime.class));
    }

    @Test
    void listarVencidos_MultiplesVencidos_DebeRetornarTodos() {
        Prestamo v1 = new Prestamo();
        v1.setId(201L);
        v1.setFechaDevolucionEsperada(LocalDateTime.now().minusDays(1));

        Prestamo v2 = new Prestamo();
        v2.setId(202L);
        v2.setFechaDevolucionEsperada(LocalDateTime.now().minusDays(10));

        when(prestamoRepository.findVencidos(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(v1, v2));

        List<Prestamo> resultado = prestamoService.listarVencidos();

        assertEquals(2, resultado.size());
        verify(prestamoRepository, times(1)).findVencidos(any(LocalDateTime.class));
    }

    // =========================================================
    // SUITE 7: buscarPorId()
    // =========================================================

    @Test
    void buscarPorId_ConIdExistente_DebeRetornarPrestamo() {
        when(prestamoRepository.findById(100L)).thenReturn(Optional.of(prestamoActivo));

        Optional<Prestamo> resultado = prestamoService.buscarPorId(100L);

        assertTrue(resultado.isPresent());
        assertEquals(100L, resultado.get().getId());
        assertEquals(Prestamo.Estado.ACTIVO, resultado.get().getEstado());
        verify(prestamoRepository, times(1)).findById(100L);
    }

    @Test
    void buscarPorId_ConIdInexistente_DebeRetornarVacio() {
        when(prestamoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Prestamo> resultado = prestamoService.buscarPorId(999L);

        assertFalse(resultado.isPresent());
        verify(prestamoRepository, times(1)).findById(999L);
    }

    // =========================================================
    // SUITE 8: isVencido() — lógica del modelo Prestamo
    // =========================================================

    @Test
    void isVencido_PrestamoActivoConFechaPasada_DebeRetornarTrue() {
        prestamoActivo.setFechaDevolucionEsperada(LocalDateTime.now().minusDays(1));

        assertTrue(prestamoActivo.isVencido());
    }

    @Test
    void isVencido_PrestamoActivoConFechaFutura_DebeRetornarFalse() {
        prestamoActivo.setFechaDevolucionEsperada(LocalDateTime.now().plusDays(5));

        assertFalse(prestamoActivo.isVencido());
    }

    @Test
    void isVencido_PrestamoDevueltoConFechaPasada_DebeRetornarFalse() {
        prestamoActivo.setEstado(Prestamo.Estado.DEVUELTO);
        prestamoActivo.setFechaDevolucionEsperada(LocalDateTime.now().minusDays(1));

        assertFalse(prestamoActivo.isVencido());
    }
}