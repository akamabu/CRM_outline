package com.vietcrm.lead.domain;

import com.vietcrm.shared.domain.BaseRecord;
import java.time.Instant;

public class Lead extends BaseRecord {
    private final String fullName;
    private final String email;
    private final String phone;
    private final String source;
    private final String ownerId;
    private final LeadStatus status;

    public Lead(String id, String tenantId, Instant createdAt, String fullName, String email, String phone, String source, String ownerId, LeadStatus status) {
        super(id, tenantId, createdAt);
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.source = source;
        this.ownerId = ownerId;
        this.status = status;
    }

    public String fullName() { return fullName; }
    public String email() { return email; }
    public String phone() { return phone; }
    public String source() { return source; }
    public String ownerId() { return ownerId; }
    public LeadStatus status() { return status; }

    public Lead assignedTo(String nextOwnerId) {
        return new Lead(id(), tenantId(), createdAt(), fullName, email, phone, source, nextOwnerId, LeadStatus.ASSIGNED);
    }

    public Lead converted() {
        return new Lead(id(), tenantId(), createdAt(), fullName, email, phone, source, ownerId, LeadStatus.CONVERTED);
    }
}
