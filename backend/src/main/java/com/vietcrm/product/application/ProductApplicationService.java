package com.vietcrm.product.application;

import com.vietcrm.product.api.ProductRequest;
import com.vietcrm.product.domain.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductApplicationService {
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    public Product create(ProductRequest request) {
        Product product = new Product(newId(), request.tenantId(), Instant.now(), request.sku(), request.name(), amount(request.price()), status(request.status()));
        products.put(product.id(), product);
        return product;
    }

    public List<Product> list(String tenantId, String keyword) {
        String normalized = normalize(keyword);
        return products.values().stream()
            .filter(product -> tenantId == null || tenantId.equals(product.tenantId()))
            .filter(product -> normalized == null || product.sku().toLowerCase().contains(normalized) || product.name().toLowerCase().contains(normalized))
            .sorted(Comparator.comparing(Product::createdAt).reversed())
            .toList();
    }

    public long count(String tenantId) {
        return list(tenantId, null).size();
    }

    private static BigDecimal amount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String status(String value) {
        return value == null || value.isBlank() ? "ACTIVE" : value.trim().toUpperCase();
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
