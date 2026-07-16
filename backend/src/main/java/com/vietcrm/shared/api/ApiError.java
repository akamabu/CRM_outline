package com.vietcrm.shared.api;

import java.util.List;

public record ApiError(String code, String message, List<FieldErrorDetail> details, String traceId) {
    public record FieldErrorDetail(String field, String code, String message) {}

    public static ApiError of(String code, String message, String traceId) {
        return new ApiError(code, message, List.of(), traceId);
    }
}
