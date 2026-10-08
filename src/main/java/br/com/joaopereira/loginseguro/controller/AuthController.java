package br.com.joaopereira.loginseguro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.joaopereira.loginseguro.dto.CadastroDto;
import br.com.joaopereira.loginseguro.service.UsuarioService;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("cadastro", new CadastroDto());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
        @Valid @ModelAttribute("cadastro") CadastroDto cadastro,
        BindingResult resultado
    ) {
        if (resultado.hasErrors()) {
            cadastro.setSenha(null);
            cadastro.setConfirmarSenha(null);
            return "cadastro";
        }

        try {
            usuarioService.cadastrar(cadastro);
        } catch (IllegalArgumentException exception) {
            resultado.rejectValue(
                "email",
                "email.duplicado",
                exception.getMessage()
            );
            cadastro.setSenha(null);
            cadastro.setConfirmarSenha(null);
            return "cadastro";
        }

        return "redirect:/login?cadastro";
    }
}