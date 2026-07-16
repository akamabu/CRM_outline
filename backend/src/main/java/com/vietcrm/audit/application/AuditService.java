package com.vietcrm.audit.application;

import com.vietcrm.audit.domain.AuditRecord;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AuditService {
    private final List<AuditRecord> records = new CopyOnWriteArrayList<>();

    public AuditRecord record(String tenantId, String actorId, String action, String targetType, String targetId, String description) {
        AuditRecord record = new AuditRecord(UUID.randomUUID().toString(), tenantId, actorId, action, targetType, targetId, Instant.now(), description);
        records.add(record);
        return record;
    }

    public List<AuditRecord> list(String tenantId, String targetId) {
        return records.stream()
            .filter(record -> tenantId == null || tenantId.equals(record.tenantId()))
            .filter(record -> targetId == null || targetId.equals(record.targetId()))
            .sorted(Comparator.comparing(AuditRecord::occurredAt).reversed())
            .toList();
    }
}
