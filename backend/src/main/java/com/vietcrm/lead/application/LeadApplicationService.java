package com.vietcrm.lead.application;

import com.vietcrm.audit.application.AuditService;
import com.vietcrm.customer.domain.Customer;
import com.vietcrm.lead.api.ConvertLeadResponse;
import com.vietcrm.lead.api.CreateLeadRequest;
import com.vietcrm.lead.domain.Lead;
import com.vietcrm.lead.domain.LeadStatus;
import com.vietcrm.notification.application.NotificationService;
import com.vietcrm.quotation.domain.Quotation;
import com.vietcrm.sales.domain.Deal;
import com.vietcrm.timeline.application.TimelineService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LeadApplicationService {
    private final Map<String, Lead> leads = new ConcurrentHashMap<>();
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();
    private final Map<String, Deal> deals = new ConcurrentHashMap<>();
    private final Map<String, Quotation> quotations = new ConcurrentHashMap<>();
    private final AuditService audit;
    private final TimelineService timeline;
    private final NotificationService notifications;
    private final AtomicInteger ownerCursor = new AtomicInteger();
    private final List<String> demoOwners = List.of("sales-001", "sales-002", "sales-003");

    public LeadApplicationService(AuditService audit, TimelineService timeline, NotificationService notifications) {
        this.audit = audit;
        this.timeline = timeline;
        this.notifications = notifications;
    }

    public Lead create(CreateLeadRequest request) {
        Optional<Lead> duplicated = findDuplicate(request.tenantId(), request.email(), request.phone());
        LeadStatus initialStatus = duplicated.isPresent() ? LeadStatus.DUPLICATED : LeadStatus.NEW;
        Lead lead = new Lead(newId(), request.tenantId(), Instant.now(), request.fullName(), normalize(request.email()), normalize(request.phone()), request.source(), null, initialStatus);
        leads.put(lead.id(), lead);
        audit.record(lead.tenantId(), "system", "LEAD_CREATED", "LEAD", lead.id(), "Lead created from " + lead.source());
        timeline.append(lead.tenantId(), "LEAD", lead.id(), "lead.created", "Lead được tạo");
        return lead;
    }

    public List<Lead> list(String tenantId) {
        return leads.values().stream()
            .filter(lead -> tenantId == null || tenantId.equals(lead.tenantId()))
            .sorted(Comparator.comparing(Lead::createdAt).reversed())
            .toList();
    }

    public long count(String tenantId) {
        return list(tenantId).size();
    }

    public Lead assign(String leadId) {
        Lead lead = requireLead(leadId);
        String owner = demoOwners.get(Math.floorMod(ownerCursor.getAndIncrement(), demoOwners.size()));
        Lead assigned = lead.assignedTo(owner);
        leads.put(assigned.id(), assigned);
        audit.record(assigned.tenantId(), owner, "LEAD_ASSIGNED", "LEAD", assigned.id(), "Lead assigned to " + owner);
        timeline.append(assigned.tenantId(), "LEAD", assigned.id(), "lead.assigned", "Lead được phân công cho " + owner);
        notifications.send(assigned.tenantId(), owner, "Lead mới", "Bạn vừa được giao lead " + assigned.fullName());
        return assigned;
    }

    public ConvertLeadResponse convert(String leadId) {
        Lead lead = requireLead(leadId);
        Lead assignedLead = lead.ownerId() == null ? assign(leadId) : lead;
        Customer customer = new Customer(newId(), assignedLead.tenantId(), Instant.now(), assignedLead.fullName(), assignedLead.email(), assignedLead.phone());
        customers.put(customer.id(), customer);

        Deal deal = new Deal(newId(), assignedLead.tenantId(), Instant.now(), customer.id(), "Cơ hội từ " + assignedLead.fullName(), BigDecimal.ZERO, "NEW");
        deals.put(deal.id(), deal);

        Quotation quotation = new Quotation(newId(), assignedLead.tenantId(), Instant.now(), deal.id(), BigDecimal.ZERO, "DRAFT");
        quotations.put(quotation.id(), quotation);

        leads.put(assignedLead.id(), assignedLead.converted());
        audit.record(assignedLead.tenantId(), assignedLead.ownerId(), "LEAD_CONVERTED", "LEAD", assignedLead.id(), "Lead converted to customer, deal and quotation");
        timeline.append(assignedLead.tenantId(), "LEAD", assignedLead.id(), "lead.converted", "Lead đã chuyển đổi thành customer/deal/quotation");
        timeline.append(customer.tenantId(), "CUSTOMER", customer.id(), "customer.created", "Customer được tạo từ lead");
        notifications.send(assignedLead.tenantId(), assignedLead.ownerId(), "Báo giá nháp", "Đã tạo báo giá nháp cho deal " + deal.name());
        return new ConvertLeadResponse(assignedLead.id(), customer.id(), deal.id(), quotation.id());
    }

    private Optional<Lead> findDuplicate(String tenantId, String email, String phone) {
        String normalizedEmail = normalize(email);
        String normalizedPhone = normalize(phone);
        return leads.values().stream().filter(lead -> lead.tenantId().equals(tenantId))
            .filter(lead -> matches(lead.email(), normalizedEmail) || matches(lead.phone(), normalizedPhone))
            .findFirst();
    }

    private Lead requireLead(String leadId) {
        Lead lead = leads.get(leadId);
        if (lead == null) {
            throw new IllegalArgumentException("Lead not found: " + leadId);
        }
        return lead;
    }

    private static boolean matches(String left, String right) {
        return left != null && !left.isBlank() && left.equals(right);
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
