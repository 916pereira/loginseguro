package br.com.joaopereira.loginseguro.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import br.com.joaopereira.loginseguro.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PaginaController {

    private final UsuarioRepository usuarioRepository;

    public PaginaController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/painel";
    }

    @GetMapping("/painel")
    public String painel(Principal principal, Model model) {
        var usuario = usuarioRepository.findByEmail(principal.getName())
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
            );

        model.addAttribute("nome", usuario.getNome());
        model.addAttribute("email", usuario.getEmail());
        model.addAttribute("role", usuario.getRole().name());

        return "painel";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado(HttpServletResponse resposta) {
        resposta.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "acesso-negado";
    }
}