package com.vietcrm.product.api;

import com.vietcrm.product.domain.Product;
import java.math.BigDecimal;

public record ProductResponse(String id, String tenantId, String sku, String name, BigDecimal price, String status) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.id(), product.tenantId(), product.sku(), product.name(), product.price(), product.status());
    }
}
