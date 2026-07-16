package com.vietcrm.task.api;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record TaskRequest(@NotBlank String tenantId, @NotBlank String title, String assigneeId, LocalDate dueDate, String priority, String status) {}
