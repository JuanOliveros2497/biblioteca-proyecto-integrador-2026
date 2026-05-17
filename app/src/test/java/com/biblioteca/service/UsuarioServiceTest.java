package com.biblioteca.service;

import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan Test");
        usuario.setEmail("juan@test.com");
        usuario.setPassword("password123");
        usuario.setRol(Usuario.Rol.USER);
        usuario.setActivo(true);
    }

    @Test
    void registrar_ConEmailNuevo_DebeGuardarUsuario() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("passwordEncriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.registrar(usuario);

        assertNotNull(resultado);
        assertEquals("Juan Test", resultado.getNombre());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void registrar_ConEmailDuplicado_DebeLanzarExcepcion() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.registrar(usuario));

        assertEquals("El email ya está registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_SinRol_DebeAsignarRolUser() {
        usuario.setRol(null);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("passwordEncriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.registrar(usuario);

        assertEquals(Usuario.Rol.USER, usuario.getRol());
    }

    @Test
    void listarTodos_DebeRetornarListaUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorEmail_ConEmailExistente_DebeRetornarUsuario() {
        when(usuarioRepository.findByEmail("juan@test.com"))
                .thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("juan@test.com");

        assertTrue(resultado.isPresent());
        assertEquals("juan@test.com", resultado.get().getEmail());
    }

    @Test
    void buscarPorEmail_ConEmailInexistente_DebeRetornarVacio() {
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("noexiste@test.com");

        assertFalse(resultado.isPresent());
    }

    @Test
    void buscarPorId_ConIdExistente_DebeRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void eliminar_DebeCallDeleteById() {
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminar(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void existeEmail_ConEmailExistente_DebeRetornarTrue() {
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(true);

        boolean resultado = usuarioService.existeEmail("juan@test.com");

        assertTrue(resultado);
    }
}