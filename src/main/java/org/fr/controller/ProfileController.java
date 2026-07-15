package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/takt/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Perfil do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(
            summary = "Obter perfil atual",
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Perfil encontrado",
                    content = @Content(
                            schema = @Schema(implementation = org.fr.dto.ProfileResponse.class),
                            examples = @ExampleObject(name = "ProfileResponse", value = """
                                    {
                                      "id": "7b2b1d4f-3a55-4b6d-9c2b-0b0fef7b8a11",
                                      "areaOfActuation": "Back-end",
                                      "role": "Desenvolvedor Back-end Pleno",
                                      "jobLevel": "Pleno"
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<ProfileResponse> get(Authentication authentication) {
        var profile = profileService.getCurrentProfile(authentication.getName());
        if (profile == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PatchMapping
    @Operation(
            summary = "Atualizar perfil atual",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProfileRequest.class),
                            examples = @ExampleObject(name = "Perfil", value = """
                                    {
                                      "areaOfActuation": "Back-end",
                                      "role": "Desenvolvedor Back-end Pleno",
                                      "jobLevel": "Pleno"
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<ProfileResponse> update(Authentication authentication, @Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(ProfileResponse.from(profileService.updateCurrentProfile(authentication.getName(), request)));
    }
}
