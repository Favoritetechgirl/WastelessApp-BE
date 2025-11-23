package com.wasteless.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void sendExpirationReminder(Long inventoryItemId) {
        log.info("Sending reminder for inventory item {}", inventoryItemId);
        // integrate SMS / Email / Push here
    }
}
