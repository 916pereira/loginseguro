package br.com.joaopereira.loginseguro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/termos")
    public String termos() {
        return "termos";
    }

    @GetMapping("/privacidade")
    public String privacidade() {
        return "privacidade";
    }
}