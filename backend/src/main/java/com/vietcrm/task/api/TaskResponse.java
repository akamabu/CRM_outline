package com.vietcrm.task.api;

import com.vietcrm.task.domain.TaskItem;
import java.time.LocalDate;

public record TaskResponse(String id, String tenantId, String title, String assigneeId, LocalDate dueDate, String priority, String status) {
    public static TaskResponse from(TaskItem task) {
        return new TaskResponse(task.id(), task.tenantId(), task.title(), task.assigneeId(), task.dueDate(), task.priority(), task.status());
    }
}
