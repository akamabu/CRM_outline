package com.vietcrm.lead.api;

import com.vietcrm.lead.application.LeadApplicationService;
import com.vietcrm.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {
    private final LeadApplicationService leads;

    public LeadController(LeadApplicationService leads) {
        this.leads = leads;
    }

    @GetMapping
    public ApiResponse<List<LeadResponse>> list(@RequestParam(required = false) String tenantId) {
        return ApiResponse.of(leads.list(tenantId).stream().map(LeadResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<LeadResponse> create(@Valid @RequestBody CreateLeadRequest request) {
        return ApiResponse.of(LeadResponse.from(leads.create(request)));
    }

    @PostMapping("/{id}/assign")
    public ApiResponse<LeadResponse> assign(@PathVariable String id) {
        return ApiResponse.of(LeadResponse.from(leads.assign(id)));
    }

    @PostMapping("/{id}/convert")
    public ApiResponse<ConvertLeadResponse> convert(@PathVariable String id) {
        return ApiResponse.of(leads.convert(id));
    }

    @PatchMapping("/{id}/contacted")
    public ApiResponse<String> markContacted(@PathVariable String id) {
        return ApiResponse.of("TODO: activity/timeline integration for lead " + id);
    }
}
