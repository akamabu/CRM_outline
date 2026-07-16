package com.vietcrm.timeline.domain;

import java.time.Instant;

public record TimelineEvent(String id, String tenantId, String aggregateType, String aggregateId, String eventType, String title, Instant occurredAt) {}
