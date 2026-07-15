package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.dto.RegisterRequest;
import org.fr.exception.*;
import org.fr.model.EmailConfirmationToken;
import org.fr.model.PasswordResetToken;
import org.fr.model.Profile;
import org.fr.model.ProductivityLevel;
import org.fr.model.User;
import org.fr.repository.EmailConfirmationTokenRepository;
import org.fr.repository.PasswordResetTokenRepository;
import org.fr.repository.ProfileRepository;
import org.fr.repository.ProductivityLevelRepository;
import org.fr.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProductivityLevelRepository productivityLevelRepository;
    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        log.info("Iniciando register");
        if (userRepository.existsByUsername(request.username())) {
            log.info("Username já existe");
            throw new UsernameAlreadyUsedException();
        }
        if (userRepository.existsByEmail(request.email())) {
            log.info("Email já existe");
            throw new EmailAlreadyUsedException();
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .emailVerified(false)
                .build();
        User savedUser = userRepository.save(user);
        createDefaultProductivityLevels(savedUser);
        log.info("Finalizando register");
        return savedUser;
    }

    public User assignProfile(String username, UUID profileId) {
        log.info("Iniciando assignProfile");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException());
        user.setProfile(profile);
        User savedUser = userRepository.save(user);
        log.info("Finalizando assignProfile");
        return savedUser;
    }

    public User loadDomainUserByUsername(String username) {
        log.info("Iniciando loadDomainUserByUsername - {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        log.info("Finalizando loadDomainUserByUsername");
        return user;
    }

    public User loadDomainUserByEmail(String email) {
        log.info("Iniciando loadDomainUserByEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        log.info("Finalizando loadDomainUserByEmail");
        return user;
    }

    public String createPasswordResetToken(String email, Duration ttl) {
        log.info("Iniciando createPasswordResetToken");
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.info("Email não encontrado para reset");
            return null;
        }
        passwordResetTokenRepository.deleteByUser_Email(email);
        String token = generateRandomToken();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hash(token))
                .expiresAt(OffsetDateTime.now().plus(ttl))
                .used(false)
                .build();
        passwordResetTokenRepository.save(resetToken);
        log.info("Finalizando createPasswordResetToken");
        return token;
    }

    public String createEmailConfirmationToken(String email, Duration ttl) {
        log.info("Iniciando createEmailConfirmationToken");
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.info("Email não encontrado para confirmação");
            return null;
        }
        emailConfirmationTokenRepository.deleteByUser_Email(email);
        String token = generateRandomToken();
        EmailConfirmationToken confirmationToken = EmailConfirmationToken.builder()
                .user(user)
                .tokenHash(hash(token))
                .expiresAt(OffsetDateTime.now().plus(ttl))
                .used(false)
                .build();
        emailConfirmationTokenRepository.save(confirmationToken);
        log.info("Finalizando createEmailConfirmationToken");
        return token;
    }

    public boolean isEmailVerified(String email) {
        log.info("Iniciando isEmailVerified");
        boolean verified = userRepository.findByEmail(email)
                .map(User::isEmailVerified)
                .orElse(false);
        log.info("Finalizando isEmailVerified");
        return verified;
    }

    public User confirmEmail(String token) {
        log.info("Iniciando confirmEmail");
        EmailConfirmationToken confirmationToken = emailConfirmationTokenRepository.findByTokenHash(hash(token))
                .orElseThrow(() -> new InvalidTokenException("Token de confirmação inválido"));
        if (confirmationToken.isUsed() || confirmationToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            log.info("Token de confirmação inválido ou expirado");
            throw new InvalidTokenException("Token de confirmação inválido");
        }
        User user = User.of(confirmationToken.getUser());
        user.setEmailVerified(true);
        EmailConfirmationToken updatedToken = EmailConfirmationToken.of(confirmationToken);
        updatedToken.setUsed(true);
        emailConfirmationTokenRepository.save(updatedToken);
        User savedUser = userRepository.save(user);
        log.info("Finalizando confirmEmail");
        return savedUser;
    }

    public User resetPassword(String token, String newPassword) {
        log.info("Iniciando resetPassword");
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHash(hash(token))
                .orElseThrow(() -> new InvalidTokenException("Token de redefinição inválido"));
        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            log.info("Token de redefinição inválido ou expirado");
            throw new InvalidTokenException("Token de redefinição inválido");
        }
        User user = User.of(resetToken.getUser());
        user.setPassword(passwordEncoder.encode(newPassword));
        PasswordResetToken updatedToken = PasswordResetToken.of(resetToken);
        updatedToken.setUsed(true);
        passwordResetTokenRepository.save(updatedToken);
        User savedUser = userRepository.save(user);
        log.info("Finalizando resetPassword");
        return savedUser;
    }

    public User updateBasicInfo(String username, org.fr.dto.UserUpdateRequest request) {
        log.info("Iniciando updateBasicInfo");
        User user = loadDomainUserByUsername(username);
        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }
        User savedUser = userRepository.save(user);
        log.info("Finalizando updateBasicInfo");
        return savedUser;
    }

    public void createDefaultProductivityLevels(User user) {
        log.info("Iniciando createDefaultProductivityLevels - {}", user.getUsername());
        productivityLevelRepository.save(ProductivityLevel.builder().user(user).displayOrder(1).name("Baixa").build());
        productivityLevelRepository.save(ProductivityLevel.builder().user(user).displayOrder(2).name("Média").build());
        productivityLevelRepository.save(ProductivityLevel.builder().user(user).displayOrder(3).name("Alta").build());
        productivityLevelRepository.save(ProductivityLevel.builder().user(user).displayOrder(4).name("Muito Alta").build());
        log.info("Finalizando createDefaultProductivityLevels");
    }

    public User getInfo(String username) {
        log.info("Iniciando getInfo");
        User user = loadDomainUserByUsername(username);
        log.info("Finalizando getInfo");
        return user;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanExpiredPasswordResetTokens() {
        log.info("Iniciando cleanExpiredPasswordResetTokens");
        passwordResetTokenRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
        log.info("Finalizando cleanExpiredPasswordResetTokens");
    }

    @Scheduled(cron = "15 0 * * * *")
    public void cleanExpiredEmailConfirmationTokens() {
        log.info("Iniciando cleanExpiredEmailConfirmationTokens");
        emailConfirmationTokenRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
        log.info("Finalizando cleanExpiredEmailConfirmationTokens");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Iniciando loadUserByUsername");
        User user = loadDomainUserByUsername(username);
        log.info("Finalizando loadUserByUsername");
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of())
                .build();
    }

    private String generateRandomToken() {
        byte[] bytes = new byte[32];
        ThreadLocalRandom.current().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao processar token", ex);
        }
    }
}
