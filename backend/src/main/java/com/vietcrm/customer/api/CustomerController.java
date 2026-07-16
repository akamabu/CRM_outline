package com.vietcrm.customer.api;

import com.vietcrm.customer.application.CustomerApplicationService;
import com.vietcrm.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerApplicationService customers;

    public CustomerController(CustomerApplicationService customers) {
        this.customers = customers;
    }

    @GetMapping
    public ApiResponse<List<CustomerResponse>> list(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String keyword) {
        return ApiResponse.of(customers.list(tenantId, keyword).stream().map(CustomerResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return ApiResponse.of(CustomerResponse.from(customers.create(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> get(@PathVariable String id) {
        return ApiResponse.of(CustomerResponse.from(customers.get(id)));
    }

    @PatchMapping("/{id}")
    public ApiResponse<CustomerResponse> update(@PathVariable String id, @Valid @RequestBody CustomerRequest request) {
        return ApiResponse.of(CustomerResponse.from(customers.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable String id) {
        customers.delete(id);
        return ApiResponse.of("deleted");
    }
}
