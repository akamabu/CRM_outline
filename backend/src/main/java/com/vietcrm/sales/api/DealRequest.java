package com.vietcrm.sales.api;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record DealRequest(@NotBlank String tenantId, @NotBlank String customerId, @NotBlank String name, BigDecimal expectedValue, String stage) {}
