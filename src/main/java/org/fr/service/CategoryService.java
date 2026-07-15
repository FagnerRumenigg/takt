package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.dto.CategoryRequest;
import org.fr.dto.CategoryResponse;
import org.fr.exception.CategoryNotFoundException;
import org.fr.exception.ForbiddenCategoryOperationException;
import org.fr.model.Category;
import org.fr.model.User;
import org.fr.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public List<CategoryResponse> list(String username) {
        log.info("Iniciando list - {}", username);
        List<CategoryResponse> categories = categoryRepository.findByUserIsNullOrUser_Username(username)
                .stream()
                .map(CategoryResponse::from)
                .toList();
        log.info("Finalizando list");
        return categories;
    }

    public CategoryResponse create(String username, CategoryRequest request) {
        log.info("Iniciando create - {}", username);
        User user = userService.loadDomainUserByUsername(username);
        Category category = categoryRepository.save(Category.builder()
                .name(request.name())
                .color(request.color())
                .user(user)
                .build());
        log.info("Finalizando create");
        return CategoryResponse.from(category);
    }

    public CategoryResponse update(String username, UUID id, CategoryRequest request) {
        log.info("Iniciando update - {} - {}", username, id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        validateOwnedCategory(username, category);
        category.setName(request.name());
        category.setColor(request.color());
        Category updatedCategory = categoryRepository.save(category);
        log.info("Finalizando update");
        return CategoryResponse.from(updatedCategory);
    }

    public void delete(String username, UUID id) {
        log.info("Iniciando delete - {} - {}", username, id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        validateOwnedCategory(username, category);
        categoryRepository.delete(category);
        log.info("Finalizando delete");
    }

    private void validateOwnedCategory(String username, Category category) {
        if (category.getUser() == null) {
            log.info("Operação bloqueada em categoria global - {}", category.getId());
            throw new ForbiddenCategoryOperationException();
        }
        if (!category.getUser().getUsername().equals(username)) {
            log.info("Operação bloqueada por dono diferente - {} != {}", category.getUser().getUsername(), username);
            throw new ForbiddenCategoryOperationException("Categoria pertence a outro usuário");
        }
    }
}
