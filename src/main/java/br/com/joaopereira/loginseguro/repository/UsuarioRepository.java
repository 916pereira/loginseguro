package br.com.joaopereira.loginseguro.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.joaopereira.loginseguro.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(
        br.com.joaopereira.loginseguro.model.Role role
    );
}