package org.fr.service;

import org.fr.dto.CategoryRequest;
import org.fr.model.Category;
import org.fr.model.User;
import org.fr.repository.CategoryRepository;
import org.fr.exception.ForbiddenCategoryOperationException;
import org.fr.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createShouldPersistUserCategory() {
        User user = User.builder().id(UUID.randomUUID()).username("fagner").build();
        when(userService.loadDomainUserByUsername("fagner")).thenReturn(user);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = categoryService.create("fagner", new CategoryRequest("Estudos", "#FF8800"));

        assertThat(response.name()).isEqualTo("Estudos");
        assertThat(response.color()).isEqualTo("#FF8800");
        assertThat(response.userId()).isEqualTo(user.getId());
    }

    @Test
    void deleteShouldRemoveOwnedCategory() {
        User user = User.builder().id(UUID.randomUUID()).username("fagner").build();
        Category category = Category.builder().id(UUID.randomUUID()).user(user).name("Estudos").build();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        categoryService.delete("fagner", category.getId());

        verify(categoryRepository).delete(category);
    }

    @Test
    void updateShouldFailWhenCategoryIsGlobal() {
        Category category = Category.builder().id(UUID.randomUUID()).name("Global").build();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.update("fagner", category.getId(), new CategoryRequest("Novo", "#000")))
                .isInstanceOf(ForbiddenCategoryOperationException.class);
    }

    @Test
    void listShouldReturnGlobalAndUserCategories() {
        when(categoryRepository.findByUserIsNullOrUser_Username("fagner")).thenReturn(java.util.List.of(
                Category.builder().name("Global").build()
        ));

        var result = categoryService.list("fagner");

        assertThat(result).hasSize(1);
    }

    @Test
    void updateShouldFailWhenMissingCategory() {
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update("fagner", UUID.randomUUID(), new CategoryRequest("Novo", "#000")))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
