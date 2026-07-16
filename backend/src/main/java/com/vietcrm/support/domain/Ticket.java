package com.vietcrm.support.domain;

import com.vietcrm.shared.domain.BaseRecord;
import java.time.Instant;

public class Ticket extends BaseRecord {
    private final String customerId;
    private final String subject;
    private final String priority;
    private final String assigneeId;
    private final String status;

    public Ticket(String id, String tenantId, Instant createdAt, String customerId, String subject, String priority, String assigneeId, String status) {
        super(id, tenantId, createdAt);
        this.customerId = customerId;
        this.subject = subject;
        this.priority = priority;
        this.assigneeId = assigneeId;
        this.status = status;
    }

    public String customerId() { return customerId; }
    public String subject() { return subject; }
    public String priority() { return priority; }
    public String assigneeId() { return assigneeId; }
    public String status() { return status; }
}
