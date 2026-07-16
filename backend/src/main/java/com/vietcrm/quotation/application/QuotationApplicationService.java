package com.vietcrm.quotation.application;

import com.vietcrm.quotation.api.QuotationRequest;
import com.vietcrm.quotation.domain.Quotation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuotationApplicationService {
    private final Map<String, Quotation> quotations = new ConcurrentHashMap<>();

    public Quotation create(QuotationRequest request) {
        Quotation quotation = new Quotation(newId(), request.tenantId(), Instant.now(), request.dealId(), amount(request.totalAmount()), status(request.status()));
        quotations.put(quotation.id(), quotation);
        return quotation;
    }

    public List<Quotation> list(String tenantId, String status) {
        return quotations.values().stream()
            .filter(quotation -> tenantId == null || tenantId.equals(quotation.tenantId()))
            .filter(quotation -> status == null || status.equalsIgnoreCase(quotation.status()))
            .sorted(Comparator.comparing(Quotation::createdAt).reversed())
            .toList();
    }

    public Quotation approve(String id) {
        Quotation existing = require(id);
        Quotation approved = new Quotation(existing.id(), existing.tenantId(), existing.createdAt(), existing.dealId(), existing.totalAmount(), "APPROVED");
        quotations.put(approved.id(), approved);
        return approved;
    }

    public long count(String tenantId) {
        return list(tenantId, null).size();
    }

    private Quotation require(String id) {
        Quotation quotation = quotations.get(id);
        if (quotation == null) {
            throw new IllegalArgumentException("Quotation not found: " + id);
        }
        return quotation;
    }

    private static BigDecimal amount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String status(String value) {
        return value == null || value.isBlank() ? "DRAFT" : value.trim().toUpperCase();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
