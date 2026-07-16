package com.vietcrm.lead.api;

import jakarta.validation.constraints.NotBlank;

public record CreateLeadRequest(
    @NotBlank String tenantId,
    @NotBlank String fullName,
    String email,
    String phone,
    String source
) {}
