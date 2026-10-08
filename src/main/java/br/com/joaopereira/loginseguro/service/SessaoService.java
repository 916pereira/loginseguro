package br.com.joaopereira.loginseguro.service;

import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
public class SessaoService {

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public SessaoService(
            FindByIndexNameSessionRepository<? extends Session> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void encerrarSessoes(String email) {
        sessionRepository.findByPrincipalName(email)
                .keySet()
                .forEach(sessionRepository::deleteById);
    }
}