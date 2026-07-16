package com.vietcrm.notification.api;

import com.vietcrm.notification.application.NotificationService;
import com.vietcrm.notification.domain.NotificationItem;
import com.vietcrm.shared.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notifications;

    public NotificationController(NotificationService notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public ApiResponse<List<NotificationItem>> list(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String recipientId) {
        return ApiResponse.of(notifications.list(tenantId, recipientId));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationItem> markRead(@PathVariable String id) {
        return ApiResponse.of(notifications.markRead(id));
    }
}
