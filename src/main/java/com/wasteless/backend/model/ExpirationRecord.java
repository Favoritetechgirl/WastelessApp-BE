package com.wasteless.backend.model;

import com.wasteless.backend.ExpirationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpirationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long inventoryItemId;

    private LocalDate reminderDate;

    private int reminderDaysBefore;

    @Enumerated(EnumType.STRING)
    private ExpirationStatus status;

    private boolean notified; // track if a reminder has already been sent
}
