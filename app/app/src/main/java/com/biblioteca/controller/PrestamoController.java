package com.biblioteca.controller;

import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.LibroService;
import com.biblioteca.service.PrestamoService;
import com.biblioteca.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final LibroService libroService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName()).orElseThrow();
        if (usuario.getRol() == Usuario.Rol.ADMIN) {
            model.addAttribute("prestamos", prestamoService.listarTodos());
        } else {
            model.addAttribute("prestamos", prestamoService.listarPorUsuario(usuario));
        }
        model.addAttribute("vencidos", prestamoService.listarVencidos());
        return "prestamos/lista";
    }

    @GetMapping("/nuevo/{libroId}")
    public String nuevoForm(@PathVariable Long libroId, Model model) {
        libroService.buscarPorId(libroId).ifPresent(libro ->
            model.addAttribute("libro", libro));
        return "prestamos/form";
    }

    @PostMapping("/nuevo/{libroId}")
    public String crear(@PathVariable Long libroId, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName()).orElseThrow();
        libroService.buscarPorId(libroId).ifPresent(libro -> {
            Prestamo prestamo = new Prestamo();
            prestamo.setUsuario(usuario);
            prestamo.setLibro(libro);
            prestamoService.crear(prestamo);
        });
        return "redirect:/prestamos";
    }

    @GetMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id) {
        prestamoService.devolver(id);
        return "redirect:/prestamos";
    }
}