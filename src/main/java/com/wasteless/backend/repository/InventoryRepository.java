package com.wasteless.backend.repository;

import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByUser(User user);
}
