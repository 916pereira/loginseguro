package br.com.joaopereira.loginseguro.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class TemaControllerAdvice {

    private final String tema;

    public TemaControllerAdvice(
            @Value("${app.theme:escuro}") String tema) {

        if (!tema.matches("[a-z][a-z0-9-]{0,39}")) {
            throw new IllegalArgumentException(
                    "O nome do tema deve usar letras minúsculas, números ou hífens.");
        }

        this.tema = tema;
    }

    @ModelAttribute("temaCss")
    public String temaCss() {
        if ("escuro".equals(tema)) {
            return null;
        }

        return "/css/themes/" + tema + ".css";
    }
}