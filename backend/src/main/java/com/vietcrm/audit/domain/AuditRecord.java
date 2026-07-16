package com.vietcrm.audit.domain;

import java.time.Instant;

public record AuditRecord(String id, String tenantId, String actorId, String action, String targetType, String targetId, Instant occurredAt, String description) {}
