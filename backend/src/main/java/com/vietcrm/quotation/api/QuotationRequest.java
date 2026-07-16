package com.vietcrm.quotation.api;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record QuotationRequest(@NotBlank String tenantId, @NotBlank String dealId, BigDecimal totalAmount, String status) {}
