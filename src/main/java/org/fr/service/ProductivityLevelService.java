package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fr.dto.ProductivityLevelRequest;
import org.fr.dto.ProductivityLevelResponse;
import org.fr.exception.InvalidTokenException;
import org.fr.model.ProductivityLevel;
import org.fr.repository.ProductivityLevelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductivityLevelService {

    private final ProductivityLevelRepository productivityLevelRepository;

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
        if (levels.size() != 4) {
            throw new InvalidTokenException("Níveis padrão não encontrados");
        }
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
}
