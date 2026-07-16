package com.vietcrm.report.api;

import com.vietcrm.customer.application.CustomerApplicationService;
import com.vietcrm.lead.application.LeadApplicationService;
import com.vietcrm.product.application.ProductApplicationService;
import com.vietcrm.quotation.application.QuotationApplicationService;
import com.vietcrm.sales.application.DealApplicationService;
import com.vietcrm.shared.api.ApiResponse;
import com.vietcrm.support.application.TicketApplicationService;
import com.vietcrm.task.application.TaskApplicationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class DashboardController {
    private final LeadApplicationService leads;
    private final CustomerApplicationService customers;
    private final DealApplicationService deals;
    private final QuotationApplicationService quotations;
    private final ProductApplicationService products;
    private final TaskApplicationService tasks;
    private final TicketApplicationService tickets;

    public DashboardController(LeadApplicationService leads, CustomerApplicationService customers, DealApplicationService deals, QuotationApplicationService quotations, ProductApplicationService products, TaskApplicationService tasks, TicketApplicationService tickets) {
        this.leads = leads;
        this.customers = customers;
        this.deals = deals;
        this.quotations = quotations;
        this.products = products;
        this.tasks = tasks;
        this.tickets = tickets;
    }

    @GetMapping("/dashboard-summary")
    public ApiResponse<DashboardSummary> dashboardSummary(@RequestParam(required = false) String tenantId) {
        return ApiResponse.of(new DashboardSummary(
            leads.count(tenantId),
            customers.count(tenantId),
            deals.count(tenantId),
            quotations.count(tenantId),
            products.count(tenantId),
            tasks.count(tenantId),
            tickets.count(tenantId)
        ));
    }
}
