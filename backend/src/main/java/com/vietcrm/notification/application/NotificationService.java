package com.vietcrm.notification.application;

import com.vietcrm.notification.domain.NotificationItem;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {
    private final List<NotificationItem> notifications = new CopyOnWriteArrayList<>();

    public NotificationItem send(String tenantId, String recipientId, String title, String message) {
        NotificationItem item = new NotificationItem(UUID.randomUUID().toString(), tenantId, recipientId, title, message, false, Instant.now());
        notifications.add(item);
        return item;
    }

    public List<NotificationItem> list(String tenantId, String recipientId) {
        return notifications.stream()
            .filter(item -> tenantId == null || tenantId.equals(item.tenantId()))
            .filter(item -> recipientId == null || recipientId.equals(item.recipientId()))
            .sorted(Comparator.comparing(NotificationItem::createdAt).reversed())
            .toList();
    }

    public NotificationItem markRead(String id) {
        for (int i = 0; i < notifications.size(); i++) {
            NotificationItem existing = notifications.get(i);
            if (existing.id().equals(id)) {
                NotificationItem updated = new NotificationItem(existing.id(), existing.tenantId(), existing.recipientId(), existing.title(), existing.message(), true, existing.createdAt());
                notifications.set(i, updated);
                return updated;
            }
        }
        throw new IllegalArgumentException("Notification not found: " + id);
    }
}
