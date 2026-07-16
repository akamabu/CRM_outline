package com.vietcrm.sales.api;

import com.vietcrm.sales.domain.Deal;
import java.math.BigDecimal;

public record DealResponse(String id, String tenantId, String customerId, String name, BigDecimal expectedValue, String stage) {
    public static DealResponse from(Deal deal) {
        return new DealResponse(deal.id(), deal.tenantId(), deal.customerId(), deal.name(), deal.expectedValue(), deal.stage());
    }
}
