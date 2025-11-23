package com.wasteless.backend.service;

import com.wasteless.backend.model.ExpirationRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpirationScheduler {
    private final ExpirationService expirationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void runDailyExpirationTasks() {
        log.info("Running daily expiration status update...");

        List<ExpirationRecord> records = expirationService
                .getAll()
                .stream()
                .map(r -> expirationService.getById(r.getId()))
                .toList();

        // update statuses
        records.forEach(expirationService::updateRecordStatus);

        // send reminders
        expirationService.sendDueReminders();
    }
}
