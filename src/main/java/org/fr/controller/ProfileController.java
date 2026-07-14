package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fr.dto.ProfileRequest;
import org.fr.dto.ProfileResponse;
import org.fr.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Perfil do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "Obter perfil atual")
    public ResponseEntity<ProfileResponse> get(Authentication authentication) {
        return ResponseEntity.ok(ProfileResponse.from(profileService.getCurrentProfile(authentication.getName())));
    }

    @PatchMapping
    @Operation(summary = "Atualizar perfil atual")
    public ResponseEntity<ProfileResponse> update(Authentication authentication, @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(ProfileResponse.from(profileService.updateCurrentProfile(authentication.getName(), request)));
    }
}
