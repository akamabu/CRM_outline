package com.vietcrm.notification.domain;

import java.time.Instant;

public record NotificationItem(String id, String tenantId, String recipientId, String title, String message, boolean read, Instant createdAt) {}
