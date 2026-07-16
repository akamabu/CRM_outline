package com.vietcrm.customer.domain;

import com.vietcrm.shared.domain.BaseRecord;
import java.time.Instant;

public class Customer extends BaseRecord {
    private final String name;
    private final String email;
    private final String phone;

    public Customer(String id, String tenantId, Instant createdAt, String name, String email, String phone) {
        super(id, tenantId, createdAt);
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String name() { return name; }
    public String email() { return email; }
    public String phone() { return phone; }
}
