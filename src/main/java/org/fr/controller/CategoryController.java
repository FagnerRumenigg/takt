package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fr.dto.CategoryRequest;
import org.fr.dto.CategoryResponse;
import org.fr.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/takt/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Categorias globais e do usuário")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar categorias globais e do usuário")
    public ResponseEntity<List<CategoryResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(categoryService.list(authentication.getName()));
    }

    @PostMapping
    @Operation(
            summary = "Criar categoria do usuário",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryRequest.class),
                            examples = @ExampleObject(name = "CategoryCreate", value = """
                                    {
                                      "name": "Estudos",
                                      "color": "#FF8800"
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<CategoryResponse> create(Authentication authentication, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.create(authentication.getName(), request));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Atualizar categoria do usuário",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryRequest.class),
                            examples = @ExampleObject(name = "CategoryUpdate", value = """
                                    {
                                      "name": "Estudos Atualizado",
                                      "color": "#00AAFF"
                                    }
                                    """)
                    )
            )
    )
    public ResponseEntity<CategoryResponse> update(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria do usuário")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        categoryService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
