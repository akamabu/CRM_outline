package com.vietcrm.lead.api;

import com.vietcrm.lead.application.LeadApplicationService;
import com.vietcrm.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/integrations/webhooks")
public class LeadWebhookController {
    private final LeadApplicationService leads;
    private final Map<String, LeadResponse> acceptedDeliveries = new ConcurrentHashMap<>();

    public LeadWebhookController(LeadApplicationService leads) {
        this.leads = leads;
    }

    @PostMapping("/leads")
    public ApiResponse<LeadResponse> receiveLead(
        @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
        @Valid @RequestBody CreateLeadRequest request
    ) {
        if (idempotencyKey != null && acceptedDeliveries.containsKey(idempotencyKey)) {
            return ApiResponse.of(acceptedDeliveries.get(idempotencyKey));
        }

        LeadResponse response = LeadResponse.from(leads.create(request));
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            acceptedDeliveries.put(idempotencyKey, response);
        }
        return ApiResponse.of(response);
    }
}
