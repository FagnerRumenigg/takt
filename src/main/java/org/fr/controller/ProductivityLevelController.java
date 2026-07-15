package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fr.dto.ProductivityLevelRequest;
import org.fr.dto.ProductivityLevelResponse;
import org.fr.service.ProductivityLevelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/takt/productivity-levels")
@RequiredArgsConstructor
@Tag(name = "Productivity Levels", description = "Níveis de produtividade do usuário")
@SecurityRequirement(name = "bearerAuth")
public class ProductivityLevelController {

    private final ProductivityLevelService productivityLevelService;

    @GetMapping
    @Operation(summary = "Listar níveis de produtividade")
    public ResponseEntity<List<ProductivityLevelResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(productivityLevelService.list(authentication.getName()));
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
}
