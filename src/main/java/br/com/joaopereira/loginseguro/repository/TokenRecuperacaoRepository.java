package br.com.joaopereira.loginseguro.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.joaopereira.loginseguro.model.TokenRecuperacao;

public interface TokenRecuperacaoRepository
    extends MongoRepository<TokenRecuperacao, String> {

    Optional<TokenRecuperacao> findByTokenHash(String tokenHash);

    void deleteByUsuarioId(String usuarioId);
}