package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.dto.AuthResponse;
import org.fr.dto.ForgotPasswordRequest;
import org.fr.dto.LoginRequest;
import org.fr.dto.RefreshRequest;
import org.fr.dto.ResendConfirmationRequest;
import org.fr.dto.ResetPasswordRequest;
import org.fr.dto.UserResponse;
import org.fr.exception.UnauthorizedException;
import org.fr.model.RefreshToken;
import org.fr.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Value("${app.password-reset-token-expiration-ms:86400000}")
    private long passwordResetTokenExpirationMs;

    @Value("${app.email-confirmation-token-expiration-ms:86400000}")
    private long emailConfirmationTokenExpirationMs;

    public AuthResponse login(LoginRequest request) {
        log.info("Iniciando login");
        User user = userService.loadDomainUserByUsername(request.username());
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.info("Credenciais inválidas no login");
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        if (!user.isEmailVerified()) {
            log.info("Login bloqueado por e-mail não confirmado");
            throw new IllegalArgumentException("E-mail ainda não confirmado");
        }
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        refreshTokenService.store(user, refreshToken, OffsetDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)));
        log.info("Finalizando login");
        return new AuthResponse(accessToken, refreshToken, jwtService.getAccessTokenExpirationMs());
    }

    public AuthResponse refresh(RefreshRequest request) {
        log.info("Iniciando refresh");
        RefreshToken token = refreshTokenService.verify(request.refreshToken());
        String accessToken = jwtService.generateAccessToken(token.getUser().getUsername());
        String refreshToken = jwtService.generateRefreshToken(token.getUser().getUsername());
        refreshTokenService.revoke(request.refreshToken());
        refreshTokenService.store(token.getUser(), refreshToken, OffsetDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)));
        log.info("Finalizando refresh");
        return new AuthResponse(accessToken, refreshToken, jwtService.getAccessTokenExpirationMs());
    }

    public void logout(String refreshToken) {
        log.info("Iniciando logout");
        refreshTokenService.revoke(refreshToken);
        log.info("Finalizando logout");
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Iniciando forgotPassword");
        String resetToken = userService.createPasswordResetToken(request.email(), Duration.ofMillis(passwordResetTokenExpirationMs));
        if (resetToken == null) {
            log.info("Email não encontrado para forgotPassword");
            return;
        }
        String resetLink = "https://localhost/reset-password?token=" + resetToken;
        mailService.sendPasswordResetEmail(request.email(), resetLink);
        log.info("Finalizando forgotPassword");
    }

    public void resetPassword(ResetPasswordRequest request) {
        log.info("Iniciando resetPassword");
        userService.resetPassword(request.token(), request.newPassword());
        log.info("Finalizando resetPassword");
    }

    public void sendConfirmationEmail(String email) {
        log.info("Iniciando sendConfirmationEmail");
        String token = userService.createEmailConfirmationToken(email, Duration.ofMillis(emailConfirmationTokenExpirationMs));
        if (token == null) {
            log.info("Email não encontrado para confirmação");
            return;
        }
        String confirmationLink = "https://localhost/confirm-email?token=" + token;
        mailService.sendEmailConfirmation(email, confirmationLink);
        log.info("Finalizando sendConfirmationEmail");
    }

    public void confirmEmail(String token) {
        log.info("Iniciando confirmEmail");
        userService.confirmEmail(token);
        log.info("Finalizando confirmEmail");
    }

    public void resendConfirmationEmail(ResendConfirmationRequest request) {
        log.info("Iniciando resendConfirmationEmail");
        if (userService.isEmailVerified(request.email())) {
            log.info("Email já confirmado");
            return;
        }
        sendConfirmationEmail(request.email());
        log.info("Finalizando resendConfirmationEmail");
    }

    public UserResponse info(Authentication authentication) {
        log.info("Iniciando info");
        Authentication currentAuthentication = authentication != null ? authentication : SecurityContextHolder.getContext().getAuthentication();
        log.debug("info authenticationClass={}, authenticationName={}",
                currentAuthentication == null ? null : currentAuthentication.getClass().getName(),
                currentAuthentication == null ? null : currentAuthentication.getName());
        if (currentAuthentication == null || currentAuthentication.getName() == null || "anonymousUser".equals(currentAuthentication.getName())) {
            log.warn("Token ausente ou inválido no info: authentication={}", currentAuthentication);
            throw new UnauthorizedException();
        }
        User user = userService.getInfo(currentAuthentication.getName());
        log.info("Finalizando info");
        return UserResponse.from(user);
    }

    public UserResponse updateUser(Authentication authentication, org.fr.dto.UserUpdateRequest request) {
        log.info("Iniciando updateUser");
        Authentication currentAuthentication = authentication != null ? authentication : SecurityContextHolder.getContext().getAuthentication();
        log.debug("updateUser authenticationClass={}, authenticationName={}",
                currentAuthentication == null ? null : currentAuthentication.getClass().getName(),
                currentAuthentication == null ? null : currentAuthentication.getName());
        if (currentAuthentication == null || currentAuthentication.getName() == null || "anonymousUser".equals(currentAuthentication.getName())) {
            log.warn("Token ausente ou inválido no updateUser: authentication={}", currentAuthentication);
            throw new UnauthorizedException();
        }
        User user = userService.updateBasicInfo(currentAuthentication.getName(), request);
        log.info("Finalizando updateUser");
        return UserResponse.from(user);
    }
}
