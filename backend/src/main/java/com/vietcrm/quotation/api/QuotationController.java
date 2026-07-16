package com.vietcrm.quotation.api;

import com.vietcrm.quotation.application.QuotationApplicationService;
import com.vietcrm.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quotations")
public class QuotationController {
    private final QuotationApplicationService quotations;

    public QuotationController(QuotationApplicationService quotations) {
        this.quotations = quotations;
    }

    @GetMapping
    public ApiResponse<List<QuotationResponse>> list(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String status) {
        return ApiResponse.of(quotations.list(tenantId, status).stream().map(QuotationResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<QuotationResponse> create(@Valid @RequestBody QuotationRequest request) {
        return ApiResponse.of(QuotationResponse.from(quotations.create(request)));
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<QuotationResponse> approve(@PathVariable String id) {
        return ApiResponse.of(QuotationResponse.from(quotations.approve(id)));
    }
}
