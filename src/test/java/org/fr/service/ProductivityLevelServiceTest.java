package org.fr.service;

import org.fr.dto.ProductivityLevelRequest;
import org.fr.dto.ProductivityLevelCreateRequest;
import org.fr.model.ProductivityLevel;
import org.fr.model.User;
import org.fr.repository.UserRepository;
import org.fr.repository.ProductivityLevelRepository;
import org.fr.service.UserService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductivityLevelServiceTest {

    @Mock private ProductivityLevelRepository productivityLevelRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;

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

    @Test
    void createShouldPersistLevel() {
        User user = User.builder().username("fagner").build();
        when(userService.loadDomainUserByUsername("fagner")).thenReturn(user);
        when(productivityLevelRepository.findByUser_UsernameOrderByDisplayOrderAsc("fagner")).thenReturn(List.of());
        when(productivityLevelRepository.save(any(ProductivityLevel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = productivityLevelService.create("fagner", new ProductivityLevelCreateRequest("Foco profundo", null));

        assertThat(result.displayOrder()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Foco profundo");
    }

    @Test
    void deleteShouldRemoveCustomLevel() {
        User user = User.builder().username("fagner").build();
        var level = ProductivityLevel.builder().id(UUID.randomUUID()).user(user).displayOrder(5).name("Foco").build();
        when(productivityLevelRepository.findById(level.getId())).thenReturn(java.util.Optional.of(level));

        productivityLevelService.delete("fagner", level.getId());

        verify(productivityLevelRepository).delete(level);
    }
}
