package com.vietcrm.sales.api;

import com.vietcrm.sales.application.DealApplicationService;
import com.vietcrm.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deals")
public class DealController {
    private final DealApplicationService deals;

    public DealController(DealApplicationService deals) {
        this.deals = deals;
    }

    @GetMapping
    public ApiResponse<List<DealResponse>> list(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String stage) {
        return ApiResponse.of(deals.list(tenantId, stage).stream().map(DealResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<DealResponse> create(@Valid @RequestBody DealRequest request) {
        return ApiResponse.of(DealResponse.from(deals.create(request)));
    }

    @PostMapping("/{id}/stage")
    public ApiResponse<DealResponse> moveStage(@PathVariable String id, @RequestParam String stage) {
        return ApiResponse.of(DealResponse.from(deals.moveStage(id, stage)));
    }
}
