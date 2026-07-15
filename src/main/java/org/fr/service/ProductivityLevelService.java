package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.dto.ProductivityLevelCreateRequest;
import org.fr.dto.ProductivityLevelRequest;
import org.fr.dto.ProductivityLevelResponse;
import org.fr.exception.InvalidTokenException;
import org.fr.model.ProductivityLevel;
import org.fr.model.User;
import org.fr.repository.ProductivityLevelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductivityLevelService {

    private final ProductivityLevelRepository productivityLevelRepository;
    private final UserService userService;

    public List<ProductivityLevelResponse> list(String username) {
        log.info("Iniciando list - {}", username);
        return productivityLevelRepository.findByUser_UsernameOrderByDisplayOrderAsc(username)
                .stream()
                .map(ProductivityLevelResponse::from)
                .toList();
    }

    public List<ProductivityLevelResponse> update(String username, List<ProductivityLevelRequest> requests) {
        log.info("Iniciando update - {}", username);
        if (requests == null || requests.size() != 4) {
            throw new InvalidTokenException("É obrigatório enviar exatamente 4 níveis");
        }
        List<ProductivityLevel> levels = productivityLevelRepository.findByUser_UsernameOrderByDisplayOrderAsc(username);
        for (ProductivityLevelRequest request : requests) {
            ProductivityLevel level = levels.stream()
                    .filter(item -> item.getDisplayOrder().equals(request.displayOrder()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidTokenException("Nível inválido: " + request.displayOrder()));
            level.setName(request.name());
        }
        productivityLevelRepository.saveAll(levels);
        log.info("Finalizando update");
        return levels.stream().map(ProductivityLevelResponse::from).toList();
    }

    public ProductivityLevelResponse create(String username, ProductivityLevelCreateRequest request) {
        log.info("Iniciando create - {}", username);
        User user = userService.loadDomainUserByUsername(username);
        Integer displayOrder = request.displayOrder();
        if (displayOrder == null) {
            displayOrder = productivityLevelRepository.findByUser_UsernameOrderByDisplayOrderAsc(username)
                    .stream()
                    .map(ProductivityLevel::getDisplayOrder)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
        }
        ProductivityLevel level = productivityLevelRepository.save(ProductivityLevel.builder()
                .user(user)
                .displayOrder(displayOrder)
                .name(request.name())
                .build());
        log.info("Finalizando create");
        return ProductivityLevelResponse.from(level);
    }

    public void delete(String username, UUID id) {
        log.info("Iniciando delete - {} - {}", username, id);
        ProductivityLevel level = productivityLevelRepository.findById(id)
                .orElseThrow(() -> new InvalidTokenException("Nível não encontrado"));
        if (!level.getUser().getUsername().equals(username)) {
            throw new InvalidTokenException("Nível pertence a outro usuário");
        }
        if (level.getDisplayOrder() != null && level.getDisplayOrder() <= 4) {
            throw new InvalidTokenException("Não é permitido excluir nível padrão");
        }
        productivityLevelRepository.delete(level);
        log.info("Finalizando delete");
    }
}
