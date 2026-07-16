package com.vietcrm.support.application;

import com.vietcrm.support.api.TicketRequest;
import com.vietcrm.support.domain.Ticket;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TicketApplicationService {
    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    public Ticket create(TicketRequest request) {
        Ticket ticket = new Ticket(newId(), request.tenantId(), Instant.now(), request.customerId(), request.subject(), value(request.priority(), "NORMAL"), request.assigneeId(), value(request.status(), "OPEN"));
        tickets.put(ticket.id(), ticket);
        return ticket;
    }

    public List<Ticket> list(String tenantId, String status) {
        return tickets.values().stream()
            .filter(ticket -> tenantId == null || tenantId.equals(ticket.tenantId()))
            .filter(ticket -> status == null || status.equalsIgnoreCase(ticket.status()))
            .sorted(Comparator.comparing(Ticket::createdAt).reversed())
            .toList();
    }

    public Ticket close(String id) {
        Ticket ticket = tickets.get(id);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + id);
        }
        Ticket closed = new Ticket(ticket.id(), ticket.tenantId(), ticket.createdAt(), ticket.customerId(), ticket.subject(), ticket.priority(), ticket.assigneeId(), "CLOSED");
        tickets.put(closed.id(), closed);
        return closed;
    }

    private static String value(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim().toUpperCase();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
