package com.vietcrm.sales.domain;

import com.vietcrm.shared.domain.BaseRecord;
import java.math.BigDecimal;
import java.time.Instant;

public class Deal extends BaseRecord {
    private final String customerId;
    private final String name;
    private final BigDecimal expectedValue;
    private final String stage;

    public Deal(String id, String tenantId, Instant createdAt, String customerId, String name, BigDecimal expectedValue, String stage) {
        super(id, tenantId, createdAt);
        this.customerId = customerId;
        this.name = name;
        this.expectedValue = expectedValue;
        this.stage = stage;
    }

    public String customerId() { return customerId; }
    public String name() { return name; }
    public BigDecimal expectedValue() { return expectedValue; }
    public String stage() { return stage; }
}
