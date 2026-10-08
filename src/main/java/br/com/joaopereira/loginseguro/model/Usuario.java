package br.com.joaopereira.loginseguro.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String nome;

    @Indexed(unique = true)
    private String email;

    private String senhaHash;

    private Role role = Role.USUARIO;

    private boolean ativo = true;

    private Instant criadoEm = Instant.now();

    private Instant termosAceitosEm;

    private String versaoTermos;

    private String versaoPoliticaPrivacidade;

    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Instant getTermosAceitosEm() {
        return termosAceitosEm;
    }

    public void setTermosAceitosEm(Instant termosAceitosEm) {
        this.termosAceitosEm = termosAceitosEm;
    }

    public String getVersaoTermos() {
        return versaoTermos;
    }

    public void setVersaoTermos(String versaoTermos) {
        this.versaoTermos = versaoTermos;
    }

    public String getVersaoPoliticaPrivacidade() {
        return versaoPoliticaPrivacidade;
    }

    public void setVersaoPoliticaPrivacidade(String versaoPoliticaPrivacidade) {
        this.versaoPoliticaPrivacidade = versaoPoliticaPrivacidade;
    }
}