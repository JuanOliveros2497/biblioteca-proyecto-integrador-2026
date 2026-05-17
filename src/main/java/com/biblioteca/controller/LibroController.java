package com.biblioteca.controller;

import com.biblioteca.model.Libro;
import com.biblioteca.service.CategoriaService;
import com.biblioteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;
    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        if (buscar != null && !buscar.isEmpty()) {
            model.addAttribute("libros", libroService.buscar(buscar));
            model.addAttribute("buscar", buscar);
        } else {
            model.addAttribute("libros", libroService.listarTodos());
        }
        return "libros/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "libros/form";
    }

    @PostMapping("/nuevo")
    public String crear(@ModelAttribute Libro libro) {
        libroService.crear(libro);
        return "redirect:/libros";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        libroService.buscarPorId(id).ifPresent(libro -> {
            model.addAttribute("libro", libro);
            model.addAttribute("categorias", categoriaService.listarTodas());
        });
        return "libros/form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Libro libro) {
        libro.setId(id);
        libroService.actualizar(libro);
        return "redirect:/libros";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return "redirect:/libros";
    }
}