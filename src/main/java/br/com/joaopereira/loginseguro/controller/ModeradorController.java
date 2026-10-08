package br.com.joaopereira.loginseguro.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.joaopereira.loginseguro.repository.UsuarioRepository;

@Controller
public class ModeradorController {

    private final UsuarioRepository usuarioRepository;

    public ModeradorController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/moderador")
    public String listar(
        @RequestParam(defaultValue = "0") int pagina,
        Model model
    ) {
        var usuarios = usuarioRepository.findAll(
            PageRequest.of(
                Math.max(0, pagina),
                10,
                Sort.by("nome").ascending().and(Sort.by("id"))
            )
        );

        model.addAttribute("usuarios", usuarios);

        return "moderador";
    }
}