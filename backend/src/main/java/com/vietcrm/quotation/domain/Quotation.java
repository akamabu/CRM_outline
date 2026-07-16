package com.vietcrm.quotation.domain;

import com.vietcrm.shared.domain.BaseRecord;
import java.math.BigDecimal;
import java.time.Instant;

public class Quotation extends BaseRecord {
    private final String dealId;
    private final BigDecimal totalAmount;
    private final String status;

    public Quotation(String id, String tenantId, Instant createdAt, String dealId, BigDecimal totalAmount, String status) {
        super(id, tenantId, createdAt);
        this.dealId = dealId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String dealId() { return dealId; }
    public BigDecimal totalAmount() { return totalAmount; }
    public String status() { return status; }
}
