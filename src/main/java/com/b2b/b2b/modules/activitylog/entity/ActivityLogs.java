package com.b2b.b2b.modules.activitylog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer entityId;
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private String performedByType;
    private Integer performedById;

    @Lob
    private String changesJson;

    @Lob
    private String metadataJson;

    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
