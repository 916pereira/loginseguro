package br.com.joaopereira.loginseguro.service;

import br.com.joaopereira.loginseguro.dto.RedefinicaoSenhaDto;
import br.com.joaopereira.loginseguro.model.TokenRecuperacao;
import br.com.joaopereira.loginseguro.model.Usuario;
import br.com.joaopereira.loginseguro.repository.TokenRecuperacaoRepository;
import br.com.joaopereira.loginseguro.repository.UsuarioRepository;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RecuperacaoSenhaService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RecuperacaoSenhaService.class);

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacaoRepository tokenRepository;
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SessaoService sessaoService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String baseUrl;

    public RecuperacaoSenhaService(
            UsuarioRepository usuarioRepository,
            TokenRecuperacaoRepository tokenRepository,
            MongoTemplate mongoTemplate,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            SessaoService sessaoService,
            @Value("${app.base-url}") String baseUrl) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.sessaoService = sessaoService;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public void solicitar(String email) {
        String emailNormalizado = email.strip().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .filter(Usuario::isAtivo)
                .orElse(null);

        if (usuario == null) {
            return;
        }

        Instant agora = Instant.now();

        Query pedidoRecente = Query.query(
                Criteria.where("usuarioId").is(usuario.getId())
                        .and("criadoEm").gt(agora.minusSeconds(60)));

        if (mongoTemplate.exists(pedidoRecente, TokenRecuperacao.class)) {
            return;
        }

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        TokenRecuperacao registro = new TokenRecuperacao();
        registro.setUsuarioId(usuario.getId());
        registro.setTokenHash(calcularHash(token));
        registro.setCriadoEm(agora);
        registro.setExpiraEm(agora.plus(Duration.ofMinutes(15)));

        tokenRepository.deleteByUsuarioId(usuario.getId());
        tokenRepository.save(registro);

        String link = baseUrl + "/redefinir-senha?token=" + token;

        try {
            emailService.enviarRecuperacao(usuario.getEmail(), link);
        } catch (MailException exception) {
            LOGGER.warn("Não foi possível enviar o e-mail de recuperação.");
        }
    }

    public boolean tokenValido(String token) {
        if (token == null || !token.matches("^[A-Za-z0-9_-]{43}$")) {
            return false;
        }

        return tokenRepository.findByTokenHash(calcularHash(token))
                .filter(registro ->
                        registro.getExpiraEm().isAfter(Instant.now()))
                .flatMap(registro ->
                        usuarioRepository.findById(registro.getUsuarioId()))
                .filter(Usuario::isAtivo)
                .isPresent();
    }

    public void redefinir(@Valid RedefinicaoSenhaDto dados) {
        String senhaHash = passwordEncoder.encode(dados.getSenha());

        Query consulta = Query.query(
                Criteria.where("tokenHash").is(calcularHash(dados.getToken()))
                        .and("expiraEm").gt(Instant.now()));

        TokenRecuperacao registro = mongoTemplate.findAndRemove(
                consulta, TokenRecuperacao.class);

        if (registro == null) {
            throw new IllegalArgumentException(
                    "Link inválido ou expirado. Solicite uma nova recuperação.");
        }

        Usuario usuario = usuarioRepository.findById(registro.getUsuarioId())
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Link inválido ou expirado. Solicite uma nova recuperação."));

        Query consultaUsuario = Query.query(
                Criteria.where("_id").is(usuario.getId())
                        .and("ativo").is(true));

        Update atualizacao = new Update().set("senhaHash", senhaHash);

        long alterados = mongoTemplate.updateFirst(
                consultaUsuario, atualizacao, Usuario.class)
                .getMatchedCount();

        if (alterados == 0) {
            throw new IllegalArgumentException(
                    "Não foi possível redefinir a senha.");
        }

        tokenRepository.deleteByUsuarioId(usuario.getId());
        sessaoService.encerrarSessoes(usuario.getEmail());
    }

    private String calcularHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 não está disponível.", exception);
        }
    }
}