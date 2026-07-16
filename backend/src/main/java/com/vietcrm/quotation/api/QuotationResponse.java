package com.vietcrm.quotation.api;

import com.vietcrm.quotation.domain.Quotation;
import java.math.BigDecimal;

public record QuotationResponse(String id, String tenantId, String dealId, BigDecimal totalAmount, String status) {
    public static QuotationResponse from(Quotation quotation) {
        return new QuotationResponse(quotation.id(), quotation.tenantId(), quotation.dealId(), quotation.totalAmount(), quotation.status());
    }
}
