package com.vietcrm.support.api;

import com.vietcrm.shared.api.ApiResponse;
import com.vietcrm.support.application.TicketApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {
    private final TicketApplicationService tickets;

    public TicketController(TicketApplicationService tickets) {
        this.tickets = tickets;
    }

    @GetMapping
    public ApiResponse<List<TicketResponse>> list(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String status) {
        return ApiResponse.of(tickets.list(tenantId, status).stream().map(TicketResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<TicketResponse> create(@Valid @RequestBody TicketRequest request) {
        return ApiResponse.of(TicketResponse.from(tickets.create(request)));
    }

    @PostMapping("/{id}/close")
    public ApiResponse<TicketResponse> close(@PathVariable String id) {
        return ApiResponse.of(TicketResponse.from(tickets.close(id)));
    }
}
