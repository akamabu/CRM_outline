package com.vietcrm.shared.api;

public record ApiResponse<T>(T data, String traceId) {
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, java.util.UUID.randomUUID().toString());
    }
}
