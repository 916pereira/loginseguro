package br.com.joaopereira.loginseguro.controller;

import java.security.Principal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.joaopereira.loginseguro.dto.UsuarioEdicaoDto;
import br.com.joaopereira.loginseguro.model.Role;
import br.com.joaopereira.loginseguro.repository.UsuarioRepository;
import br.com.joaopereira.loginseguro.service.UsuarioService;
import jakarta.validation.Valid;

@Controller
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AdminController(
        UsuarioRepository usuarioRepository,
        UsuarioService usuarioService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/admin")
    public String listar(
        @RequestParam(defaultValue = "0") int pagina,
        Principal principal,
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
        model.addAttribute("emailAdministrador", principal.getName());

        return "admin";
    }

    @GetMapping("/admin/usuarios/{id}/editar")
    public String editar(
        @PathVariable String id,
        Model model,
        RedirectAttributes atributos
    ) {
        try {
            var usuario = usuarioService.buscarPorId(id);

            UsuarioEdicaoDto dados = new UsuarioEdicaoDto();
            dados.setNome(usuario.getNome());
            dados.setEmail(usuario.getEmail());
            dados.setRole(usuario.getRole());
            dados.setAtivo(usuario.isAtivo());

            model.addAttribute("edicao", dados);
            prepararFormulario(id, model);

            return "usuario-editar";
        } catch (IllegalArgumentException exception) {
            atributos.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/admin/usuarios/{id}/editar")
    public String atualizar(
        @PathVariable String id,
        @Valid @ModelAttribute("edicao") UsuarioEdicaoDto dados,
        BindingResult resultado,
        Principal principal,
        Model model,
        RedirectAttributes atributos
    ) {
        if (resultado.hasErrors()) {
            prepararFormulario(id, model);
            return "usuario-editar";
        }

        try {
            usuarioService.atualizar(id, dados, principal.getName());
        } catch (IllegalArgumentException exception) {
            resultado.reject("edicao.invalida", exception.getMessage());
            prepararFormulario(id, model);
            return "usuario-editar";
        }

        atributos.addFlashAttribute(
            "sucesso",
            "Usuário atualizado com sucesso."
        );

        return "redirect:/admin";
    }

    @PostMapping("/admin/usuarios/{id}/excluir")
    public String excluir(
        @PathVariable String id,
        Principal principal,
        RedirectAttributes atributos
    ) {
        try {
            usuarioService.excluir(id, principal.getName());
            atributos.addFlashAttribute(
                "sucesso",
                "Usuário excluído com sucesso."
            );
        } catch (IllegalArgumentException exception) {
            atributos.addFlashAttribute("erro", exception.getMessage());
        }

        return "redirect:/admin";
    }

    private void prepararFormulario(String id, Model model) {
        model.addAttribute("usuarioId", id);
        model.addAttribute("roles", Role.values());
    }
}