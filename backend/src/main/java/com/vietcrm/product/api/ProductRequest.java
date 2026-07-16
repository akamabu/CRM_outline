package com.vietcrm.product.api;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ProductRequest(@NotBlank String tenantId, @NotBlank String sku, @NotBlank String name, BigDecimal price, String status) {}
