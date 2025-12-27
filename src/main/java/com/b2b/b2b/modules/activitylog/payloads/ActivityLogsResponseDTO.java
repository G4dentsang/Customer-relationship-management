package com.b2b.b2b.modules.activitylog.payloads;

import java.time.LocalDateTime;

public record ActivityLogsResponseDTO(
        Long id,
        Integer entityId,
        String entityType,
        String performedByType,
        Integer performedById,
        Object changes,
        Object metadata,
        LocalDateTime createdAt
) {
}
