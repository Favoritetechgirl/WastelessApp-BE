package com.wasteless.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryItemId;

    private String name;
    private int quantity;
    private String category;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    private String storageLocation; // e.g., "Fridge", "Freezer", "Pantry"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
