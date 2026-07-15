package org.fr.controller;

import org.fr.dto.ProductivityLevelRequest;
import org.fr.dto.ProductivityLevelResponse;
import org.fr.service.ProductivityLevelService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductivityLevelControllerTest {

    @Test
    void listShouldDelegateToService() {
        ProductivityLevelService service = mock(ProductivityLevelService.class);
        ProductivityLevelController controller = new ProductivityLevelController(service);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(service.list("fagner")).thenReturn(List.of());

        ResponseEntity<List<ProductivityLevelResponse>> response = controller.list(authentication);

        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void updateShouldDelegateToService() {
        ProductivityLevelService service = mock(ProductivityLevelService.class);
        ProductivityLevelController controller = new ProductivityLevelController(service);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(service.update(eq("fagner"), any())).thenReturn(List.of());

        ResponseEntity<List<ProductivityLevelResponse>> response = controller.update(authentication, List.of(
                new ProductivityLevelRequest(1, "Baixa"),
                new ProductivityLevelRequest(2, "Média"),
                new ProductivityLevelRequest(3, "Alta"),
                new ProductivityLevelRequest(4, "Muito Alta")
        ));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
