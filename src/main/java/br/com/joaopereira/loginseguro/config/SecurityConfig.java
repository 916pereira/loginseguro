package br.com.joaopereira.loginseguro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/login",
                                "/cadastro",
                                "/termos",
                                "/privacidade",
                                "/esqueci-senha",
                                "/redefinir-senha",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/error")
                        .permitAll()
                        .requestMatchers("/admin", "/admin/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers("/moderador", "/moderador/**")
                        .hasAnyRole("MODERADOR", "ADMINISTRADOR")
                        .anyRequest()
                        .authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("senha")
                        .defaultSuccessUrl("/painel", true)
                        .failureUrl("/login?erro")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "SESSION"))
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acesso-negado"))
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation
                                .changeSessionId()));

        return http.build();
    }
}