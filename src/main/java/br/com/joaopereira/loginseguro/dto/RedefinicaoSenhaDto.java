package br.com.joaopereira.loginseguro.dto;

import java.nio.charset.StandardCharsets;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RedefinicaoSenhaDto {

    @NotBlank(message = "Token de recuperação inválido.")
    @Pattern(
        regexp = "^[A-Za-z0-9_-]{43}$",
        message = "Token de recuperação inválido."
    )
    private String token;

    @NotBlank(message = "Informe uma senha.")
    @Size(min = 8, max = 64, message = "A senha deve ter entre 8 e 64 caracteres.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).*$",
        message = "A senha deve conter letra maiúscula, minúscula e número."
    )
    private String senha;

    @NotBlank(message = "Confirme sua senha.")
    private String confirmarSenha;

    @AssertTrue(message = "As senhas não coincidem.")
    public boolean isSenhasIguais() {
        return senha != null && senha.equals(confirmarSenha);
    }

    @AssertTrue(message = "A senha deve ter no máximo 72 bytes em UTF-8.")
    public boolean isSenhaDentroDoLimite() {
        return senha == null
            || senha.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}