package br.com.joaopereira.loginseguro.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remetente;

    public EmailService(
        JavaMailSender mailSender,
        @Value("${app.mail.from}") String remetente
    ) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    public void enviarRecuperacao(String destinatario, String link) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject("Redefinição de senha | Login Seguro");
        mensagem.setText(
            "Recebemos uma solicitação para redefinir sua senha.\n\n"
            + "Use o link abaixo para criar uma nova senha:\n"
            + link
            + "\n\nO link é válido por 15 minutos e pode ser usado uma única vez."
            + "\nSe você não fez essa solicitação, ignore este e-mail."
            + "\n\nLogin Seguro"
        );

        mailSender.send(mensagem);
    }
}