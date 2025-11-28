package com.wasteless.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int quantity;
    private String category;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    private String storageLocation; // e.g., "Fridge", "Freezer", "Pantry"

    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.ACTIVE; // Default to ACTIVE

    private LocalDateTime consumedAt; // When item was marked as eaten/wasted

    private Double estimatedValue; // Estimated value in Naira for impact calculations

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public enum ItemStatus {
        ACTIVE,   // Still in inventory
        EATEN,    // Successfully consumed
        WASTED    // Thrown away/expired
    }
}
