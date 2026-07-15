package org.fr.repository;

import org.fr.model.ProductivityLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductivityLevelRepository extends JpaRepository<ProductivityLevel, UUID> {
    List<ProductivityLevel> findByUser_UsernameOrderByDisplayOrderAsc(String username);
    void deleteByUser_Username(String username);
}
