package com.vietcrm.support.api;

import jakarta.validation.constraints.NotBlank;

public record TicketRequest(@NotBlank String tenantId, String customerId, @NotBlank String subject, String priority, String assigneeId, String status) {}
