package com.concerttracker.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String from;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Renderiza una plantilla HTML de src/main/resources/templates/email y la envía.
     * No relanza la excepción: un correo fallido no debe tumbar el hilo asíncrono que lo llama.
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            String html = templateEngine.process("email/" + templateName, context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Correo enviado a {} con asunto '{}'", to, subject);
        } catch (MessagingException | MailException e) {
            log.error("No se pudo enviar el correo a {}: {}", to, e.getMessage());
        }
    }
}
