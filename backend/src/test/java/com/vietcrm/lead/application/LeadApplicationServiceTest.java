package com.vietcrm.lead.application;

import com.vietcrm.audit.application.AuditService;
import com.vietcrm.lead.api.ConvertLeadResponse;
import com.vietcrm.lead.api.CreateLeadRequest;
import com.vietcrm.lead.domain.Lead;
import com.vietcrm.lead.domain.LeadStatus;
import com.vietcrm.notification.application.NotificationService;
import com.vietcrm.timeline.application.TimelineService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LeadApplicationServiceTest {
    private final AuditService audit = new AuditService();
    private final TimelineService timeline = new TimelineService();
    private final NotificationService notifications = new NotificationService();
    private final LeadApplicationService service = new LeadApplicationService(audit, timeline, notifications);

    @Test
    void createsAssignsAndConvertsLeadWithAuditTimelineAndNotifications() {
        Lead lead = service.create(new CreateLeadRequest("tenant-1", "Nguyen Van A", "A@Example.com", "0900000001", "website"));

        assertThat(lead.status()).isEqualTo(LeadStatus.NEW);
        assertThat(audit.list("tenant-1", lead.id())).hasSize(1);
        assertThat(timeline.forAggregate(lead.id())).hasSize(1);

        Lead assigned = service.assign(lead.id());
        assertThat(assigned.ownerId()).isEqualTo("sales-001");
        assertThat(notifications.list("tenant-1", "sales-001")).hasSize(1);

        ConvertLeadResponse conversion = service.convert(assigned.id());
        assertThat(conversion.customerId()).isNotBlank();
        assertThat(conversion.dealId()).isNotBlank();
        assertThat(conversion.quotationId()).isNotBlank();
        assertThat(audit.list("tenant-1", assigned.id())).hasSize(3);
        assertThat(timeline.forAggregate(assigned.id())).hasSize(3);
        assertThat(notifications.list("tenant-1", "sales-001")).hasSize(2);
    }

    @Test
    void marksSecondLeadAsDuplicatedByEmail() {
        service.create(new CreateLeadRequest("tenant-1", "First", "dup@example.com", null, "website"));

        Lead duplicated = service.create(new CreateLeadRequest("tenant-1", "Second", "DUP@example.com", null, "website"));

        assertThat(duplicated.status()).isEqualTo(LeadStatus.DUPLICATED);
    }
}
