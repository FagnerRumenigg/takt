package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fr.dto.ProductivityLevelCreateRequest;
import org.fr.dto.ProductivityLevelRequest;
import org.fr.dto.ProductivityLevelResponse;
import org.fr.service.ProductivityLevelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/takt/productivity-levels")
@RequiredArgsConstructor
@Tag(name = "Productivity Levels", description = "Níveis de produtividade do usuário")
@SecurityRequirement(name = "bearerAuth")
public class ProductivityLevelController {

    private final ProductivityLevelService productivityLevelService;

    @GetMapping
    @Operation(
            summary = "Listar níveis de produtividade",
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lista de níveis",
                    content = @Content(
                            schema = @Schema(implementation = ProductivityLevelResponse.class),
                            examples = @ExampleObject(name = "Levels", value = """
                                    [
                                      { "id": "33333333-3333-3333-3333-333333333333", "displayOrder": 1, "name": "Baixa" },
                                      { "id": "44444444-4444-4444-4444-444444444444", "displayOrder": 2, "name": "Média" }
                                    ]
                                    """)
                    )
            )
    )
    public ResponseEntity<List<ProductivityLevelResponse>> list(Authentication authentication) {
        List<ProductivityLevelResponse> levels = productivityLevelService.list(authentication.getName());
        if (levels.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(levels);
    }

    @PatchMapping
    @Operation(
            summary = "Atualizar nomes dos quatro níveis",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProductivityLevelRequest[].class),
                            examples = @ExampleObject(name = "UpdateLevels", value = """
                                    [
                                      { "displayOrder": 1, "name": "Baixa" },
                                      { "displayOrder": 2, "name": "Média" },
                                      { "displayOrder": 3, "name": "Alta" },
                                      { "displayOrder": 4, "name": "Muito Alta" }
                                    ]
                                    """)
                    )
            )
    )
    public ResponseEntity<List<ProductivityLevelResponse>> update(Authentication authentication, @Valid @RequestBody List<ProductivityLevelRequest> request) {
        return ResponseEntity.ok(productivityLevelService.update(authentication.getName(), request));
    }

    @PostMapping
    @Operation(hidden = true)
    public ResponseEntity<ProductivityLevelResponse> create(Authentication authentication, @Valid @RequestBody ProductivityLevelCreateRequest request) {
        return ResponseEntity.ok(productivityLevelService.create(authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    @Operation(hidden = true)
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable("id") UUID id) {
        productivityLevelService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
