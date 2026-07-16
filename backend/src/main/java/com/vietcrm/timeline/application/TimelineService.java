package com.vietcrm.timeline.application;

import com.vietcrm.timeline.domain.TimelineEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TimelineService {
    private final List<TimelineEvent> events = new CopyOnWriteArrayList<>();

    public TimelineEvent append(String tenantId, String aggregateType, String aggregateId, String eventType, String title) {
        TimelineEvent event = new TimelineEvent(UUID.randomUUID().toString(), tenantId, aggregateType, aggregateId, eventType, title, Instant.now());
        events.add(event);
        return event;
    }

    public List<TimelineEvent> forAggregate(String aggregateId) {
        return events.stream()
            .filter(event -> aggregateId.equals(event.aggregateId()))
            .sorted(Comparator.comparing(TimelineEvent::occurredAt).reversed())
            .toList();
    }
}
