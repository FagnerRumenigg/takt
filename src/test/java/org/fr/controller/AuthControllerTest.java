package org.fr.controller;

import org.fr.dto.AuthResponse;
import org.fr.dto.UserUpdateRequest;
import org.fr.dto.RegisterRequest;
import org.fr.model.User;
import org.fr.service.AuthService;
import org.fr.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Test
    void registerShouldDelegateToService() {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(userService, authService);

        User user = User.builder().email("fagner@gmail.com").build();
        when(userService.register(any())).thenReturn(user);

        ResponseEntity<?> response = controller.register(new RegisterRequest("fagner", "fagner@gmail.com", "Senha@123"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(authService).sendConfirmationEmail("fagner@gmail.com");
    }

    @Test
    void loginShouldDelegateToService() {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(userService, authService);

        when(authService.login(any())).thenReturn(new AuthResponse("a", "r", 1L));

        ResponseEntity<AuthResponse> response = controller.login(new org.fr.dto.LoginRequest("fagner", "Senha@123"));

        assertThat(response.getBody().accessToken()).isEqualTo("a");
    }

    @Test
    void infoShouldDelegateToService() {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(userService, authService);
        Authentication authentication = mock(Authentication.class);
        when(authService.info(authentication)).thenReturn(org.fr.dto.UserResponse.of(null, "fagner", "mail", null, null, null));

        ResponseEntity<?> response = controller.info(authentication);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void updateUserShouldDelegateToService() {
        UserService userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(userService, authService);
        Authentication authentication = mock(Authentication.class);
        when(authService.updateUser(eq(authentication), any(UserUpdateRequest.class)))
                .thenReturn(org.fr.dto.UserResponse.of(null, "fagner", "mail", "Fagner", LocalDate.of(1995, 8, 10), null));

        ResponseEntity<?> response = controller.updateUser(authentication, new UserUpdateRequest("Fagner", LocalDate.of(1995, 8, 10)));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
