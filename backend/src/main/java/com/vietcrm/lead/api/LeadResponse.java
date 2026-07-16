package com.vietcrm.lead.api;

import com.vietcrm.lead.domain.Lead;

public record LeadResponse(String id, String tenantId, String fullName, String email, String phone, String source, String ownerId, String status) {
    public static LeadResponse from(Lead lead) {
        return new LeadResponse(lead.id(), lead.tenantId(), lead.fullName(), lead.email(), lead.phone(), lead.source(), lead.ownerId(), lead.status().name());
    }
}
