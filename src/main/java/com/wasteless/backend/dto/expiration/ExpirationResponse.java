package com.wasteless.backend.dto.expiration;

import com.wasteless.backend.ExpirationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExpirationResponse {
    private Long id;
    private Long inventoryItemId;
    private LocalDate expiryDate;
    private LocalDate reminderDate;
    private int reminderDaysBefore;
    private ExpirationStatus status;
    private boolean notified;
}
