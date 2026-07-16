package com.vietcrm.support.api;

import com.vietcrm.support.domain.Ticket;

public record TicketResponse(String id, String tenantId, String customerId, String subject, String priority, String assigneeId, String status) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(ticket.id(), ticket.tenantId(), ticket.customerId(), ticket.subject(), ticket.priority(), ticket.assigneeId(), ticket.status());
    }
}
