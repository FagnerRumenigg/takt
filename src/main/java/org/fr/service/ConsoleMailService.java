package org.fr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConsoleMailService implements MailService {
    @Override
    public void sendPasswordResetEmail(String email, String resetLink) {
        log.info("=== PASSWORD RESET EMAIL ===");
        log.info("To: {}", email);
        log.info("Subject: Redefinição de senha");
        log.info("Link: {}", resetLink);
        log.info("=== END PASSWORD RESET EMAIL ===");
    }

    @Override
    public void sendEmailConfirmation(String email, String confirmationLink) {
        log.info("=== EMAIL CONFIRMATION ===");
        log.info("To: {}", email);
        log.info("Subject: Confirmação de e-mail");
        log.info("Link: {}", confirmationLink);
        log.info("=== END EMAIL CONFIRMATION ===");
    }
}
