package com.vietcrm.customer.api;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(@NotBlank String tenantId, @NotBlank String name, String email, String phone, String ownerId, String status) {}
