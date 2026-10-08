package br.com.joaopereira.loginseguro.config;

import org.mongodb.spring.session.config.annotation.web.http.EnableMongoHttpSession;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMongoHttpSession(
    collectionName = "sessoes",
    maxInactiveIntervalInSeconds = 1800
)
public class SessionConfig {
}