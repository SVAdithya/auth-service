package com.app.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user_id", columnList = "userId"),
        @Index(name = "idx_audit_event_type", columnList = "eventType"),
        @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @Column(name = "event_id", length = 36)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "audit_log_details", joinColumns = @JoinColumn(name = "event_id"))
    @MapKeyColumn(name = "detail_key", length = 100)
    @Column(name = "detail_value", length = 500)
    private Map<String, String> details;

    // Factory method for creating new audit log
    public static AuditLog create(String eventType, String userId, Map<String, String> details) {
        return new AuditLog(
                UUID.randomUUID().toString(),
                eventType,
                userId,
                OffsetDateTime.now(),
                Map.copyOf(details)
        );
    }

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = OffsetDateTime.now();
        }
    }
}