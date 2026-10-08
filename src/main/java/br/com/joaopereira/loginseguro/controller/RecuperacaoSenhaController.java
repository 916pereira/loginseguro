package br.com.joaopereira.loginseguro.controller;

import br.com.joaopereira.loginseguro.dto.RecuperacaoSenhaDto;
import br.com.joaopereira.loginseguro.dto.RedefinicaoSenhaDto;
import br.com.joaopereira.loginseguro.service.RecuperacaoSenhaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    public RecuperacaoSenhaController(
            RecuperacaoSenhaService recuperacaoSenhaService) {
        this.recuperacaoSenhaService = recuperacaoSenhaService;
    }

    @GetMapping("/esqueci-senha")
    public String formularioRecuperacao(Model model) {
        model.addAttribute("recuperacao", new RecuperacaoSenhaDto());
        return "esqueci-senha";
    }

    @PostMapping("/esqueci-senha")
    public String solicitarRecuperacao(
            @Valid @ModelAttribute("recuperacao") RecuperacaoSenhaDto dados,
            BindingResult resultado,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            return "esqueci-senha";
        }

        recuperacaoSenhaService.solicitar(dados.getEmail());

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Se o e-mail pertencer a uma conta ativa, você receberá "
                        + "um link de recuperação. Confira também o spam.");

        return "redirect:/esqueci-senha";
    }

    @GetMapping("/redefinir-senha")
    public String formularioRedefinicao(
            @RequestParam(required = false) String token,
            Model model) {

        boolean tokenValido = recuperacaoSenhaService.tokenValido(token);

        RedefinicaoSenhaDto dados = new RedefinicaoSenhaDto();
        dados.setToken(tokenValido ? token : "");

        model.addAttribute("redefinicao", dados);
        model.addAttribute("tokenValido", tokenValido);

        return "redefinir-senha";
    }

    @PostMapping("/redefinir-senha")
    public String redefinirSenha(
            @Valid @ModelAttribute("redefinicao") RedefinicaoSenhaDto dados,
            BindingResult resultado,
            Model model) {

        boolean tokenValido =
                recuperacaoSenhaService.tokenValido(dados.getToken());

        model.addAttribute("tokenValido", tokenValido);

        if (!tokenValido || resultado.hasErrors()) {
            limparSenhas(dados);
            return "redefinir-senha";
        }

        try {
            recuperacaoSenhaService.redefinir(dados);
        } catch (IllegalArgumentException exception) {
            resultado.reject("redefinicao", exception.getMessage());
            model.addAttribute(
                    "tokenValido",
                    recuperacaoSenhaService.tokenValido(dados.getToken()));
            limparSenhas(dados);
            return "redefinir-senha";
        }

        return "redirect:/login?senhaRedefinida";
    }

    private void limparSenhas(RedefinicaoSenhaDto dados) {
        dados.setSenha(null);
        dados.setConfirmarSenha(null);
    }
}