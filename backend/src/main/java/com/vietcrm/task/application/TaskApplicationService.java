package com.vietcrm.task.application;

import com.vietcrm.task.api.TaskRequest;
import com.vietcrm.task.domain.TaskItem;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskApplicationService {
    private final Map<String, TaskItem> tasks = new ConcurrentHashMap<>();

    public TaskItem create(TaskRequest request) {
        TaskItem task = new TaskItem(newId(), request.tenantId(), Instant.now(), request.title(), request.assigneeId(), request.dueDate(), value(request.priority(), "NORMAL"), value(request.status(), "OPEN"));
        tasks.put(task.id(), task);
        return task;
    }

    public List<TaskItem> list(String tenantId, String assigneeId) {
        return tasks.values().stream()
            .filter(task -> tenantId == null || tenantId.equals(task.tenantId()))
            .filter(task -> assigneeId == null || assigneeId.equals(task.assigneeId()))
            .sorted(Comparator.comparing(TaskItem::createdAt).reversed())
            .toList();
    }

    public long count(String tenantId) {
        return list(tenantId, null).size();
    }

    public TaskItem complete(String id) {
        TaskItem task = tasks.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + id);
        }
        TaskItem completed = new TaskItem(task.id(), task.tenantId(), task.createdAt(), task.title(), task.assigneeId(), task.dueDate(), task.priority(), "COMPLETED");
        tasks.put(completed.id(), completed);
        return completed;
    }

    private static String value(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim().toUpperCase();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
