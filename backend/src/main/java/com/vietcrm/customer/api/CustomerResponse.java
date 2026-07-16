package com.vietcrm.customer.api;

import com.vietcrm.customer.domain.Customer;

public record CustomerResponse(String id, String tenantId, String name, String email, String phone) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.id(), customer.tenantId(), customer.name(), customer.email(), customer.phone());
    }
}
