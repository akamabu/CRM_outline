package com.vietcrm.sales.application;

import com.vietcrm.sales.api.DealRequest;
import com.vietcrm.sales.domain.Deal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DealApplicationService {
    private final Map<String, Deal> deals = new ConcurrentHashMap<>();

    public Deal create(DealRequest request) {
        Deal deal = new Deal(newId(), request.tenantId(), Instant.now(), request.customerId(), request.name(), amount(request.expectedValue()), stage(request.stage()));
        deals.put(deal.id(), deal);
        return deal;
    }

    public List<Deal> list(String tenantId, String stage) {
        return deals.values().stream()
            .filter(deal -> tenantId == null || tenantId.equals(deal.tenantId()))
            .filter(deal -> stage == null || stage.equalsIgnoreCase(deal.stage()))
            .sorted(Comparator.comparing(Deal::createdAt).reversed())
            .toList();
    }

    public long count(String tenantId) {
        return list(tenantId, null).size();
    }

    public Deal moveStage(String id, String nextStage) {
        Deal existing = get(id);
        Deal moved = new Deal(existing.id(), existing.tenantId(), existing.createdAt(), existing.customerId(), existing.name(), existing.expectedValue(), stage(nextStage));
        deals.put(moved.id(), moved);
        return moved;
    }

    public Deal get(String id) {
        Deal deal = deals.get(id);
        if (deal == null) {
            throw new IllegalArgumentException("Deal not found: " + id);
        }
        return deal;
    }

    private static BigDecimal amount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String stage(String value) {
        return value == null || value.isBlank() ? "NEW" : value.trim().toUpperCase();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
