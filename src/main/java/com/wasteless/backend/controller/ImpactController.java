package com.wasteless.backend.controller;

import com.wasteless.backend.dto.impact.ImpactHistoryResponse;
import com.wasteless.backend.dto.impact.ImpactSummaryResponse;
import com.wasteless.backend.dto.impact.UpdateItemStatusRequest;
import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.User;
import com.wasteless.backend.service.ImpactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/impact")
@RequiredArgsConstructor
public class ImpactController {

    private final ImpactService impactService;

    /**
     * GET /api/v1/impact/summary
     * Get current month's impact summary
     */
    @GetMapping("/summary")
    public ResponseEntity<ImpactSummaryResponse> getCurrentMonthSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = (User) userDetails;
        ImpactSummaryResponse summary = impactService.getCurrentMonthSummary(user);
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /api/v1/impact/summary/30days
     * Get last 30 days impact summary
     */
    @GetMapping("/summary/30days")
    public ResponseEntity<ImpactSummaryResponse> getLast30DaysSummary(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = (User) userDetails;
        ImpactSummaryResponse summary = impactService.getLast30DaysSummary(user);
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /api/v1/impact/history
     * Get historical impact data (default 6 months)
     */
    @GetMapping("/history")
    public ResponseEntity<ImpactHistoryResponse> getImpactHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "6") int months
    ) {
        User user = (User) userDetails;
        ImpactHistoryResponse history = impactService.getImpactHistory(user, months);
        return ResponseEntity.ok(history);
    }

    /**
     * PUT /api/v1/impact/items/{itemId}/status
     * Update item status (mark as eaten or wasted)
     */
    @PutMapping("/items/{itemId}/status")
    public ResponseEntity<InventoryItem> updateItemStatus(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        InventoryItem updatedItem = impactService.updateItemStatus(
                itemId,
                request.getStatus(),
                request.getEstimatedValue()
        );
        return ResponseEntity.ok(updatedItem);
    }
}
