package br.com.joaopereira.loginseguro.dto;

import java.nio.charset.StandardCharsets;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CadastroDto {

    @NotBlank(message = "Informe seu nome.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe seu e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres.")
    private String email;

    @NotBlank(message = "Informe uma senha.")
    @Size(min = 8, max = 64, message = "A senha deve ter entre 8 e 64 caracteres.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).*$",
        message = "A senha deve conter letra maiúscula, minúscula e número."
    )
    private String senha;

    @NotBlank(message = "Confirme sua senha.")
    private String confirmarSenha;

    @AssertTrue(message = "Aceite os Termos de Uso e declare que leu a Política de Privacidade.")
    private boolean aceitouTermos;

    @AssertTrue(message = "As senhas não coincidem.")
    public boolean isSenhasIguais() {
        return senha != null && senha.equals(confirmarSenha);
    }

    @AssertTrue(message = "A senha deve ter no máximo 72 bytes em UTF-8.")
    public boolean isSenhaDentroDoLimite() {
        return senha == null
            || senha.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isAceitouTermos() {
        return aceitouTermos;
    }

    public void setAceitouTermos(boolean aceitouTermos) {
        this.aceitouTermos = aceitouTermos;
    }
}