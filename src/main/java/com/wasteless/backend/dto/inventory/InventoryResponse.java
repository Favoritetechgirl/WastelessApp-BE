package com.wasteless.backend.dto.inventory;

import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long inventoryItemId;
    private String name;
    private int quantity;
    private String category;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String storageLocation;
    private long daysUntilExpiry;

    // Helper method to compute days left
    public static InventoryResponse fromEntity(com.wasteless.backend.model.InventoryItem item) {
        long daysLeft = 0;
        if (item.getExpiryDate() != null) {
            daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), item.getExpiryDate());
        }

        return InventoryResponse.builder()
                .inventoryItemId(item.getInventoryItemId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .category(item.getCategory())
                .purchaseDate(item.getPurchaseDate())
                .expiryDate(item.getExpiryDate())
                .storageLocation(item.getStorageLocation())
                .daysUntilExpiry(daysLeft)
                .build();
    }
}
