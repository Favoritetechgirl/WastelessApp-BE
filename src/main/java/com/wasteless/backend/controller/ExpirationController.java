package com.wasteless.backend.controller;

import com.wasteless.backend.dto.expiration.ExpirationRequest;
import com.wasteless.backend.dto.expiration.ExpirationResponse;
import com.wasteless.backend.service.ExpirationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/expiration")
@RequiredArgsConstructor
public class ExpirationController {
    private final ExpirationService expirationService;

    @PostMapping("/create")
    public ExpirationResponse createExpiration(@RequestBody ExpirationRequest request) {
        return expirationService.createRecord(request);
    }

    @GetMapping("/all")
    public List<ExpirationResponse> getAll() {
        return expirationService.getAll();
    }
//    @GetMapping("/{id}")
//    public ExpirationResponse getById(@PathVariable Long id) {
//        return expirationService.getById(id);
//    }
//
//    // UPDATE STATUS
//    @PatchMapping("/{id}/status")
//    public ExpirationResponse updateRecordStatus(
//            @PathVariable Long id,
//            @RequestParam String status
//    ) {
//        return expirationService.updateRecordStatus();
//    }

    @PostMapping("/send-reminders")
    public String sendDueReminders() {
        expirationService.sendDueReminders();
        return "Due expiration reminders sent successfully.";
    }
}
