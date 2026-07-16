package com.vietcrm.audit.api;

import com.vietcrm.audit.application.AuditService;
import com.vietcrm.audit.domain.AuditRecord;
import com.vietcrm.shared.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditController {
    private final AuditService audit;

    public AuditController(AuditService audit) {
        this.audit = audit;
    }

    @GetMapping
    public ApiResponse<List<AuditRecord>> list(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String targetId) {
        return ApiResponse.of(audit.list(tenantId, targetId));
    }
}
