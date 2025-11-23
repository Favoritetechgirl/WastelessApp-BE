package com.wasteless.backend.dto.inventory;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {
    private String name;
    private int quantity;
    private String category;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String storageLocation;
}
