package org.fr.service;

import org.fr.dto.ProductivityLevelRequest;
import org.fr.model.ProductivityLevel;
import org.fr.model.User;
import org.fr.repository.ProductivityLevelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductivityLevelServiceTest {

    @Mock private ProductivityLevelRepository productivityLevelRepository;

    @InjectMocks private ProductivityLevelService productivityLevelService;

    @Test
    void listShouldReturnLevelsInOrder() {
        when(productivityLevelRepository.findByUser_UsernameOrderByDisplayOrderAsc("fagner"))
                .thenReturn(List.of(ProductivityLevel.builder().displayOrder(1).name("Baixa").build()));

        var result = productivityLevelService.list("fagner");

        assertThat(result).hasSize(1);
    }

    @Test
    void updateShouldRenameLevels() {
        User user = User.builder().username("fagner").build();
        var levels = List.of(
                ProductivityLevel.builder().id(UUID.randomUUID()).user(user).displayOrder(1).name("Baixa").build(),
                ProductivityLevel.builder().id(UUID.randomUUID()).user(user).displayOrder(2).name("Média").build(),
                ProductivityLevel.builder().id(UUID.randomUUID()).user(user).displayOrder(3).name("Alta").build(),
                ProductivityLevel.builder().id(UUID.randomUUID()).user(user).displayOrder(4).name("Muito Alta").build()
        );
        when(productivityLevelRepository.findByUser_UsernameOrderByDisplayOrderAsc("fagner")).thenReturn(levels);
        when(productivityLevelRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = productivityLevelService.update("fagner", List.of(
                new ProductivityLevelRequest(1, "N1"),
                new ProductivityLevelRequest(2, "N2"),
                new ProductivityLevelRequest(3, "N3"),
                new ProductivityLevelRequest(4, "N4")
        ));

        assertThat(result.get(0).name()).isEqualTo("N1");
    }

    @Test
    void updateShouldFailWithWrongSize() {
        assertThatThrownBy(() -> productivityLevelService.update("fagner", List.of(
                new ProductivityLevelRequest(1, "N1")
        ))).isInstanceOf(org.fr.exception.InvalidTokenException.class);
    }
}
