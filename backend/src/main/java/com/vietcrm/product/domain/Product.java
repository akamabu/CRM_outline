package com.vietcrm.product.domain;

import com.vietcrm.shared.domain.BaseRecord;
import java.math.BigDecimal;
import java.time.Instant;

public class Product extends BaseRecord {
    private final String sku;
    private final String name;
    private final BigDecimal price;
    private final String status;

    public Product(String id, String tenantId, Instant createdAt, String sku, String name, BigDecimal price, String status) {
        super(id, tenantId, createdAt);
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public String sku() { return sku; }
    public String name() { return name; }
    public BigDecimal price() { return price; }
    public String status() { return status; }
}
