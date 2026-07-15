package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fr.dto.AuthResponse;
import org.fr.dto.ForgotPasswordRequest;
import org.fr.dto.LoginRequest;
import org.fr.dto.LogoutRequest;
import org.fr.dto.RefreshRequest;
import org.fr.dto.ResendConfirmationRequest;
import org.fr.dto.RegisterRequest;
import org.fr.dto.ResetPasswordRequest;
import org.fr.dto.UserUpdateRequest;
import org.fr.dto.UserResponse;
import org.fr.model.User;
import org.fr.service.AuthService;
import org.fr.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/takt/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticação e recuperação de senha")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Cadastrar usuário",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Cadastro",
                                    value = """
                                            {
                                              "username": "fagner",
                                              "email": "fagner@gmail.com",
                                              "password": "Senha@123"
                                            }
                                            """
                            )
                    )
            )
    )
    public ResponseEntity<UserResponse> register(@Valid @org.springframework.web.bind.annotation.RequestBody RegisterRequest request) {
        User user = userService.register(request);
        authService.sendConfirmationEmail(user.getEmail());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login com username e senha",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Login",
                                    value = """
                                            {
                                              "username": "fagner",
                                              "password": "Senha@123"
                                            }
                                            """
                            )
                    )
            )
    )
    public ResponseEntity<AuthResponse> login(@Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/info")
    @Operation(
            summary = "Retorna os dados básicos do usuário autenticado",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> info(Authentication authentication) {
        return ResponseEntity.ok(authService.info(authentication));
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Solicitar redefinição de senha",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ForgotPasswordRequest.class),
                            examples = @ExampleObject(name = "Forgot", value = """
                                    { "email": "fagner@gmail.com" }
                                    """)
                    )
            )
    )
    public ResponseEntity<Void> forgotPassword(@Valid @org.springframework.web.bind.annotation.RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Redefinir senha com token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResetPasswordRequest.class),
                            examples = @ExampleObject(name = "Reset", value = """
                                    {
                                      "token": "token-longo-gerado-no-link",
                                      "newPassword": "SenhaNova@123"
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<Void> resetPassword(@Valid @org.springframework.web.bind.annotation.RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm-email")
    @Operation(summary = "Confirmar e-mail com token")
    public ResponseEntity<Void> confirmEmail(@RequestParam("token") String token) {
        authService.confirmEmail(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-confirmation")
    @Operation(
            summary = "Reenviar e-mail de confirmação",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ResendConfirmationRequest.class),
                            examples = @ExampleObject(name = "Resend", value = """
                                    { "email": "fagner@gmail.com" }
                                    """)
                    )
            )
    )
    public ResponseEntity<Void> resendConfirmation(@Valid @org.springframework.web.bind.annotation.RequestBody ResendConfirmationRequest request) {
        authService.resendConfirmationEmail(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revogar refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/user")
    @Operation(
            summary = "Atualizar nome completo e data de nascimento",
            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserUpdateRequest.class),
                            examples = @ExampleObject(name = "UpdateUser", value = """
                                    {
                                      "fullName": "Fagner Ramos",
                                      "birthDate": "10/08/1995"
                                    }
                                    """)
                    )
            )
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> updateUser(Authentication authentication, @Valid @org.springframework.web.bind.annotation.RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(authService.updateUser(authentication, request));
    }
}
