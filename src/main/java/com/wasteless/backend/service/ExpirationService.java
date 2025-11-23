package com.wasteless.backend.service;

import com.wasteless.backend.ExpirationStatus;
import com.wasteless.backend.dto.expiration.ExpirationRequest;
import com.wasteless.backend.dto.expiration.ExpirationResponse;
import com.wasteless.backend.model.ExpirationRecord;
import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.repository.ExpirationRecordRepository;
import com.wasteless.backend.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpirationService {
    private final ExpirationRecordRepository expirationRepo;
    private final InventoryRepository inventoryRepo;
    private final NotificationService notificationService;

    public ExpirationResponse createRecord(ExpirationRequest request) {

        InventoryItem item = inventoryRepo.findById(request.getInventoryItemId())
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        ExpirationRecord record = ExpirationRecord.builder()
                .inventoryItemId(item.getInventoryItemId())
                .reminderDaysBefore(request.getReminderDaysBefore())
                .status(calculateStatus(item.getExpiryDate()))
                .notified(false)
                .build();

        expirationRepo.save(record);

        return toResponse(record, item.getExpiryDate());
    }

    public ExpirationStatus calculateStatus(LocalDate expiryDate) {
        LocalDate today = LocalDate.now();

        if (today.isAfter(expiryDate)) return ExpirationStatus.EXPIRED;
        if (expiryDate.minusDays(3).isBefore(today)) return ExpirationStatus.EXPIRING_SOON;

        return ExpirationStatus.FRESH;
    }

    public void updateRecordStatus(ExpirationRecord record) {
        InventoryItem item = inventoryRepo.findById(record.getInventoryItemId())
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        record.setStatus(calculateStatus(item.getExpiryDate()));
        expirationRepo.save(record);
    }

    public List<ExpirationResponse> getAll() {
        return expirationRepo.findAll().stream()
                .map(rec -> {
                    InventoryItem item = inventoryRepo.findById(rec.getInventoryItemId())
                            .orElseThrow();
                    return toResponse(rec, item.getExpiryDate());
                })
                .toList();
    }

    public ExpirationRecord getById(Long id) {
        return expirationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Expiration record with ID " + id + " not found"
                ));
    }

    public void sendDueReminders() {
        List<ExpirationRecord> records = expirationRepo.findAll();

        for (ExpirationRecord record : records) {
            InventoryItem item = inventoryRepo.findById(record.getInventoryItemId())
                    .orElseThrow();

            LocalDate reminderDate = item.getExpiryDate().minusDays(record.getReminderDaysBefore());
            LocalDate today = LocalDate.now();

            if (!record.isNotified() && today.isEqual(reminderDate)) {
                notificationService.sendExpirationReminder(record.getInventoryItemId());
                record.setNotified(true);
                expirationRepo.save(record);
            }
        }
    }

    private ExpirationResponse toResponse(ExpirationRecord record, LocalDate expiryDate) {
        return ExpirationResponse.builder()
                .id(record.getId())
                .inventoryItemId(record.getInventoryItemId())
                .expiryDate(LocalDate.parse(expiryDate.toString()))
                .reminderDaysBefore(record.getReminderDaysBefore())
                .notified(record.isNotified())
                .status(record.getStatus())
                .build();
    }
}
