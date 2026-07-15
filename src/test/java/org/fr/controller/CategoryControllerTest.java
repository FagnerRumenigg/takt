package org.fr.controller;

import org.fr.dto.CategoryRequest;
import org.fr.dto.CategoryResponse;
import org.fr.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.UUID;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Test
    void listShouldDelegateToService() {
        CategoryService categoryService = mock(CategoryService.class);
        CategoryController controller = new CategoryController(categoryService);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(categoryService.list("fagner")).thenReturn(List.of());

        ResponseEntity<List<CategoryResponse>> response = controller.list(authentication);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void createShouldDelegateToService() {
        CategoryService categoryService = mock(CategoryService.class);
        CategoryController controller = new CategoryController(categoryService);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(categoryService.create(eq("fagner"), any())).thenReturn(CategoryResponse.of(null, "Estudos", "#FF8800", null, null));

        ResponseEntity<CategoryResponse> response = controller.create(authentication, new CategoryRequest("Estudos", "#FF8800"));

        assertThat(response.getBody().name()).isEqualTo("Estudos");
    }

    @Test
    void updateShouldDelegateToService() {
        CategoryService categoryService = mock(CategoryService.class);
        CategoryController controller = new CategoryController(categoryService);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(categoryService.update(eq("fagner"), any(UUID.class), any())).thenReturn(CategoryResponse.of(UUID.randomUUID(), "Estudos", "#FF8800", null, null));

        ResponseEntity<CategoryResponse> response = controller.update(authentication, UUID.randomUUID(), new CategoryRequest("Estudos", "#FF8800"));

        assertThat(response.getBody().name()).isEqualTo("Estudos");
    }

    @Test
    void deleteShouldDelegateToService() {
        CategoryService categoryService = mock(CategoryService.class);
        CategoryController controller = new CategoryController(categoryService);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");

        ResponseEntity<Void> response = controller.delete(authentication, UUID.randomUUID());

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(categoryService).delete(eq("fagner"), any(UUID.class));
    }
}
