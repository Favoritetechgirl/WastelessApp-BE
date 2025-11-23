package com.wasteless.backend.service;

import com.wasteless.backend.dto.impact.ImpactHistoryResponse;
import com.wasteless.backend.dto.impact.ImpactSummaryResponse;
import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImpactService {

    private final InventoryRepository inventoryRepository;

    // Average CO2 emission per kg of food waste (in kg CO2)
    // Based on research: 1 kg of food waste = ~2.5 kg CO2 equivalent
    private static final double CO2_PER_KG_FOOD = 2.5;

    // Average weight per food item (in kg) - this is a simplified assumption
    private static final double AVG_WEIGHT_PER_ITEM = 0.5;

    // Default estimated value per item if not specified (in Naira)
    private static final double DEFAULT_ITEM_VALUE = 500.0;

    /**
     * Get current month's impact summary for a user
     */
    public ImpactSummaryResponse getCurrentMonthSummary(User user) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        return calculateImpactForPeriod(user, startOfMonth, now, "This Month");
    }

    /**
     * Get last 30 days impact summary
     */
    public ImpactSummaryResponse getLast30DaysSummary(User user) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime now = LocalDateTime.now();

        return calculateImpactForPeriod(user, thirtyDaysAgo, now, "Last 30 Days");
    }

    /**
     * Get historical impact data (monthly breakdown)
     */
    public ImpactHistoryResponse getImpactHistory(User user, int months) {
        List<ImpactHistoryResponse.MonthlyImpact> monthlyData = new ArrayList<>();

        for (int i = 0; i < months; i++) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            ImpactSummaryResponse monthSummary = calculateImpactForPeriod(
                    user, startOfMonth, endOfMonth,
                    yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
            );

            ImpactHistoryResponse.MonthlyImpact monthlyImpact = ImpactHistoryResponse.MonthlyImpact.builder()
                    .month(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                    .moneySaved(monthSummary.getMoneySaved())
                    .co2Saved(monthSummary.getCo2Saved())
                    .itemsSaved(monthSummary.getItemsSaved())
                    .itemsWasted(monthSummary.getItemsWasted())
                    .wasteReductionPercentage(monthSummary.getWasteReductionPercentage())
                    .build();

            monthlyData.add(monthlyImpact);
        }

        // Calculate total impact across all time
        ImpactSummaryResponse totalImpact = calculateImpactForPeriod(
                user,
                LocalDateTime.of(2000, 1, 1, 0, 0), // Far past date
                LocalDateTime.now(),
                "All Time"
        );

        return ImpactHistoryResponse.builder()
                .monthlyData(monthlyData)
                .totalImpact(totalImpact)
                .build();
    }

    /**
     * Calculate impact metrics for a specific period
     */
    private ImpactSummaryResponse calculateImpactForPeriod(
            User user,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String period
    ) {
        // Get all items consumed/wasted in this period
        List<InventoryItem> consumedItems = inventoryRepository.findAll().stream()
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .filter(item -> item.getConsumedAt() != null)
                .filter(item -> !item.getConsumedAt().isBefore(startDate))
                .filter(item -> !item.getConsumedAt().isAfter(endDate))
                .toList();

        // Separate eaten and wasted items
        List<InventoryItem> eatenItems = consumedItems.stream()
                .filter(item -> item.getStatus() == InventoryItem.ItemStatus.EATEN)
                .toList();

        List<InventoryItem> wastedItems = consumedItems.stream()
                .filter(item -> item.getStatus() == InventoryItem.ItemStatus.WASTED)
                .toList();

        // Calculate financial impact
        double moneySaved = calculateTotalValue(eatenItems);
        double moneyWasted = calculateTotalValue(wastedItems);

        // Calculate environmental impact (CO2)
        double co2Saved = calculateCO2Impact(eatenItems);
        double co2Wasted = calculateCO2Impact(wastedItems);

        // Calculate volume impact
        int itemsSaved = eatenItems.size();
        int itemsWasted = wastedItems.size();
        int totalItems = consumedItems.size();

        // Calculate waste reduction percentage
        double wasteReductionPercentage = totalItems > 0
                ? (itemsSaved * 100.0 / totalItems)
                : 0.0;

        return ImpactSummaryResponse.builder()
                .moneySaved(moneySaved)
                .moneyWasted(moneyWasted)
                .co2Saved(co2Saved)
                .co2Wasted(co2Wasted)
                .itemsSaved(itemsSaved)
                .itemsWasted(itemsWasted)
                .totalItems(totalItems)
                .wasteReductionPercentage(Math.round(wasteReductionPercentage * 10.0) / 10.0)
                .period(period)
                .build();
    }

    /**
     * Calculate total value of items
     */
    private double calculateTotalValue(List<InventoryItem> items) {
        return items.stream()
                .mapToDouble(item -> {
                    double itemValue = item.getEstimatedValue() != null
                            ? item.getEstimatedValue()
                            : DEFAULT_ITEM_VALUE;
                    return itemValue * item.getQuantity();
                })
                .sum();
    }

    /**
     * Calculate CO2 impact
     * Formula: number of items × avg weight per item × CO2 per kg
     */
    private double calculateCO2Impact(List<InventoryItem> items) {
        double totalWeight = items.stream()
                .mapToDouble(item -> item.getQuantity() * AVG_WEIGHT_PER_ITEM)
                .sum();

        double co2Impact = totalWeight * CO2_PER_KG_FOOD;

        // Round to 2 decimal places
        return Math.round(co2Impact * 100.0) / 100.0;
    }

    /**
     * Update item status (mark as eaten or wasted)
     */
    public InventoryItem updateItemStatus(Long itemId, InventoryItem.ItemStatus status, Double estimatedValue) {
        InventoryItem item = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        item.setStatus(status);
        item.setConsumedAt(LocalDateTime.now());

        if (estimatedValue != null) {
            item.setEstimatedValue(estimatedValue);
        }

        return inventoryRepository.save(item);
    }
}
