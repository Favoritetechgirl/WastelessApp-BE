package com.wasteless.backend.dto.expiration;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpirationRequest {
    private Long inventoryItemId;
    private int reminderDaysBefore = 3; // default
}
