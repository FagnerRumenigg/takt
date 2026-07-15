package org.fr.service;

import org.fr.dto.AuthResponse;
import org.fr.dto.ForgotPasswordRequest;
import org.fr.dto.LoginRequest;
import org.fr.dto.RefreshRequest;
import org.fr.dto.ResendConfirmationRequest;
import org.fr.dto.ResetPasswordRequest;
import org.fr.dto.UserUpdateRequest;
import org.fr.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserService userService;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private MailService mailService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private AuthService authService;

    @Test
    void loginShouldReturnTokens() {
        User user = User.builder().username("fagner").password("hashed").emailVerified(true).build();
        when(userService.loadDomainUserByUsername("fagner")).thenReturn(user);
        when(passwordEncoder.matches("Senha@123", "hashed")).thenReturn(true);
        when(jwtService.generateAccessToken("fagner")).thenReturn("access");
        when(jwtService.generateRefreshToken("fagner")).thenReturn("refresh");
        when(jwtService.getAccessTokenExpirationMs()).thenReturn(3600000L);

        AuthResponse response = authService.login(new LoginRequest("fagner", "Senha@123"));

        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh");
        assertThat(response.accessTokenExpiresInMs()).isEqualTo(3600000L);
    }

    @Test
    void refreshShouldRotateTokens() {
        User user = User.builder().username("fagner").build();
        var refreshToken = org.fr.model.RefreshToken.builder().user(user).build();
        when(refreshTokenService.verify("refresh")).thenReturn(refreshToken);
        when(jwtService.generateAccessToken("fagner")).thenReturn("access2");
        when(jwtService.generateRefreshToken("fagner")).thenReturn("refresh2");
        when(jwtService.getAccessTokenExpirationMs()).thenReturn(3600000L);

        AuthResponse response = authService.refresh(new RefreshRequest("refresh"));

        assertThat(response.accessToken()).isEqualTo("access2");
        verify(refreshTokenService).revoke("refresh");
        verify(refreshTokenService).store(eq(user), eq("refresh2"), any());
    }

    @Test
    void forgotPasswordShouldSendMail() {
        when(userService.createPasswordResetToken(eq("fagner@gmail.com"), any())).thenReturn("token");

        authService.forgotPassword(new ForgotPasswordRequest("fagner@gmail.com"));

        verify(mailService).sendPasswordResetEmail(eq("fagner@gmail.com"), contains("token=token"));
    }

    @Test
    void resetPasswordShouldDelegate() {
        authService.resetPassword(new ResetPasswordRequest("token", "SenhaNova@123"));
        verify(userService).resetPassword("token", "SenhaNova@123");
    }

    @Test
    void resendConfirmationShouldSkipWhenVerified() {
        when(userService.isEmailVerified("fagner@gmail.com")).thenReturn(true);

        authService.resendConfirmationEmail(new ResendConfirmationRequest("fagner@gmail.com"));

        verify(mailService, never()).sendEmailConfirmation(anyString(), anyString());
    }

    @Test
    void infoShouldReturnUserResponse() {
        User user = User.builder().username("fagner").email("fagner@gmail.com").build();
        when(userService.getInfo("fagner")).thenReturn(user);
        var authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("fagner");

        var response = authService.info(authentication);

        assertThat(response.email()).isEqualTo("fagner@gmail.com");
    }

    @Test
    void updateUserShouldDelegate() {
        User user = User.builder().username("fagner").email("fagner@gmail.com").build();
        when(userService.updateBasicInfo(eq("fagner"), any(UserUpdateRequest.class))).thenReturn(user);
        var authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("fagner");

        var response = authService.updateUser(authentication, new UserUpdateRequest("Fagner", java.time.LocalDate.of(1995, 8, 10)));

        assertThat(response.username()).isEqualTo("fagner");
    }
}
