package br.com.joaopereira.loginseguro.service;

import java.util.Locale;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.joaopereira.loginseguro.model.Usuario;
import br.com.joaopereira.loginseguro.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        String emailNormalizado = email.strip().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
            .orElseThrow(() ->
                new UsernameNotFoundException("Credenciais inválidas.")
            );

        return User.withUsername(usuario.getEmail())
            .password(usuario.getSenhaHash())
            .roles(usuario.getRole().name())
            .disabled(!usuario.isAtivo())
            .build();
    }
}