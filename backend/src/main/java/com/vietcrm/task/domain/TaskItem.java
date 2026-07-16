package com.vietcrm.task.domain;

import com.vietcrm.shared.domain.BaseRecord;
import java.time.Instant;
import java.time.LocalDate;

public class TaskItem extends BaseRecord {
    private final String title;
    private final String assigneeId;
    private final LocalDate dueDate;
    private final String priority;
    private final String status;

    public TaskItem(String id, String tenantId, Instant createdAt, String title, String assigneeId, LocalDate dueDate, String priority, String status) {
        super(id, tenantId, createdAt);
        this.title = title;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    public String title() { return title; }
    public String assigneeId() { return assigneeId; }
    public LocalDate dueDate() { return dueDate; }
    public String priority() { return priority; }
    public String status() { return status; }
}
