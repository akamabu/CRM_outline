package com.vietcrm.shared.domain;

import java.time.Instant;

public abstract class BaseRecord {
    private final String id;
    private final String tenantId;
    private final Instant createdAt;

    protected BaseRecord(String id, String tenantId, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
    }

    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public Instant createdAt() { return createdAt; }
}
