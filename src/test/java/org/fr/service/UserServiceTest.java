package org.fr.service;

import org.fr.dto.RegisterRequest;
import org.fr.dto.UserUpdateRequest;
import org.fr.exception.EmailAlreadyUsedException;
import org.fr.exception.InvalidTokenException;
import org.fr.model.User;
import org.fr.model.PasswordResetToken;
import org.fr.model.EmailConfirmationToken;
import org.fr.repository.EmailConfirmationTokenRepository;
import org.fr.repository.PasswordResetTokenRepository;
import org.fr.repository.ProfileRepository;
import org.fr.repository.ProductivityLevelRepository;
import org.fr.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private ProductivityLevelRepository productivityLevelRepository;
    @Mock private EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    @Test
    void registerShouldPersistUser() {
        when(userRepository.existsByUsername("fagner")).thenReturn(false);
        when(userRepository.existsByEmail("fagner@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.register(new RegisterRequest("fagner", "fagner@gmail.com", "Senha@123"));

        assertThat(user.getUsername()).isEqualTo("fagner");
        assertThat(user.getEmail()).isEqualTo("fagner@gmail.com");
        assertThat(user.getPassword()).isEqualTo("hashed");
    }

    @Test
    void updateBasicInfoShouldUpdateFields() {
        User user = User.builder().username("fagner").email("fagner@gmail.com").build();
        when(userRepository.findByUsername("fagner")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateBasicInfo("fagner", new UserUpdateRequest("Fagner Ramos", LocalDate.of(1995, 8, 10)));

        assertThat(updated.getFullName()).isEqualTo("Fagner Ramos");
        assertThat(updated.getBirthDate()).isEqualTo(LocalDate.of(1995, 8, 10));
    }

    @Test
    void registerShouldFailWhenEmailExists() {
        when(userRepository.existsByUsername("fagner")).thenReturn(false);
        when(userRepository.existsByEmail("fagner@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(new RegisterRequest("fagner", "fagner@gmail.com", "Senha@123")))
                .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void createEmailConfirmationTokenShouldReturnNullForMissingUser() {
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        String token = userService.createEmailConfirmationToken("missing@email.com", java.time.Duration.ofHours(1));

        assertThat(token).isNull();
    }

    @Test
    void confirmEmailShouldThrowForInvalidToken() {
        when(emailConfirmationTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.confirmEmail("invalid"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void resetPasswordShouldThrowForInvalidToken() {
        when(passwordResetTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resetPassword("invalid", "SenhaNova@123"))
                .isInstanceOf(InvalidTokenException.class);
    }
}
