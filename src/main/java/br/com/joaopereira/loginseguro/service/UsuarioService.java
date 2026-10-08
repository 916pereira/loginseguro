package br.com.joaopereira.loginseguro.service;

import java.time.Instant;
import java.util.Locale;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import br.com.joaopereira.loginseguro.dto.CadastroDto;
import br.com.joaopereira.loginseguro.dto.UsuarioEdicaoDto;
import br.com.joaopereira.loginseguro.model.Role;
import br.com.joaopereira.loginseguro.model.Usuario;
import br.com.joaopereira.loginseguro.repository.TokenRecuperacaoRepository;
import br.com.joaopereira.loginseguro.repository.UsuarioRepository;
import jakarta.validation.Valid;

@Service
@Validated
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessaoService sessaoService;
    private final TokenRecuperacaoRepository tokenRepository;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        SessaoService sessaoService,
        TokenRecuperacaoRepository tokenRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessaoService = sessaoService;
        this.tokenRepository = tokenRepository;
    }

    public void cadastrar(@Valid CadastroDto cadastro) {
        if (!cadastro.isAceitouTermos()) {
            throw new IllegalArgumentException(
                "É necessário aceitar os Termos de Uso e ler a Política de Privacidade."
            );
        }

        String email = normalizarEmail(cadastro.getEmail());

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(cadastro.getNome().strip());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(cadastro.getSenha()));
        usuario.setRole(Role.USUARIO);
        usuario.setAtivo(true);
        usuario.setTermosAceitosEm(Instant.now());
        usuario.setVersaoTermos("1.0");
        usuario.setVersaoPoliticaPrivacidade("1.0");

        salvar(usuario);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Usuário não encontrado.")
            );
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void atualizar(
        String id,
        @Valid UsuarioEdicaoDto dados,
        String emailAdministrador
    ) {
        Usuario usuario = buscarPorId(id);
        String email = normalizarEmail(dados.getEmail());
        String emailAnterior = usuario.getEmail();

        if (emailAnterior.equals(emailAdministrador)) {
            if (!email.equals(emailAnterior)
                || dados.getRole() != Role.ADMINISTRADOR
                || !dados.isAtivo()) {
                throw new IllegalArgumentException(
                    "Você pode alterar seu nome, mas deve manter seu e-mail, perfil e conta ativa."
                );
            }
        }

        var mesmoEmail = usuarioRepository.findByEmail(email);

        if (mesmoEmail.isPresent()
            && !mesmoEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        boolean acessoAlterado = !emailAnterior.equals(email)
            || usuario.getRole() != dados.getRole()
            || usuario.isAtivo() != dados.isAtivo();

        usuario.setNome(dados.getNome().strip());
        usuario.setEmail(email);
        usuario.setRole(dados.getRole());
        usuario.setAtivo(dados.isAtivo());

        salvar(usuario);

        if (acessoAlterado) {
            sessaoService.encerrarSessoes(emailAnterior);

            if (!emailAnterior.equals(email)) {
                sessaoService.encerrarSessoes(email);
            }

            tokenRepository.deleteByUsuarioId(id);
        }
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void excluir(String id, String emailAdministrador) {
        Usuario usuario = buscarPorId(id);

        if (usuario.getEmail().equals(emailAdministrador)) {
            throw new IllegalArgumentException(
                "Você não pode excluir sua própria conta."
            );
        }

        usuarioRepository.delete(usuario);
        sessaoService.encerrarSessoes(usuario.getEmail());
        tokenRepository.deleteByUsuarioId(id);
    }

    private String normalizarEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private void salvar(Usuario usuario) {
        try {
            usuarioRepository.save(usuario);
        } catch (DuplicateKeyException exception) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }
    }
}