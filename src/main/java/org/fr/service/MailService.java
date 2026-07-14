package org.fr.service;

public interface MailService {
    void sendPasswordResetEmail(String email, String resetLink);
    void sendEmailConfirmation(String email, String confirmationLink);
}
