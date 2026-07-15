package org.fr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(
            summary = "Listar categorias globais e do usuário",
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lista de categorias",
                    content = @Content(
                            schema = @Schema(implementation = CategoryResponse.class),
                            examples = @ExampleObject(name = "CategoryList", value = """
                                    [
                                      {
                                        "id": "11111111-1111-1111-1111-111111111111",
                                        "name": "Programação",
                                        "color": "#4F46E5",
                                        "global": true
                                      },
                                      {
                                        "id": "22222222-2222-2222-2222-222222222222",
                                        "name": "Estudos",
                                        "color": "#F97316",
                                        "global": false
                                      }
                                    ]
                                    """)
                    )
            )
    )
    public ResponseEntity<List<CategoryResponse>> list(Authentication authentication) {
        List<CategoryResponse> categories = categoryService.list(authentication.getName());
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
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
    public ResponseEntity<CategoryResponse> update(Authentication authentication, @PathVariable("id") UUID id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria do usuário")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable("id") UUID id) {
        categoryService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
