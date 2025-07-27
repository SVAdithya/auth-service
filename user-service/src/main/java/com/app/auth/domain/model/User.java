package com.app.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_status", columnList = "status")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", length = 50)
    private List<String> roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled;

    // Factory method for creating new user
    public static User create(String email, String name, String passwordHash, List<String> roles) {
        OffsetDateTime now = OffsetDateTime.now();
        return new User(
                UUID.randomUUID().toString(),
                email,
                name,
                passwordHash,
                List.copyOf(roles),
                UserStatus.ACTIVE,
                now,
                now,
                false
        );
    }

    // Factory method for updating user profile
    public static User withUpdatedName(User existing, String name) {
        return new User(
                existing.id,
                existing.email,
                name,
                existing.passwordHash,
                existing.roles,
                existing.status,
                existing.createdAt,
                OffsetDateTime.now(),
                existing.mfaEnabled
        );
    }

    // Factory method for updating user status
    public static User withUpdatedStatus(User existing, UserStatus status) {
        return new User(
                existing.id,
                existing.email,
                existing.name,
                existing.passwordHash,
                existing.roles,
                status,
                existing.createdAt,
                OffsetDateTime.now(),
                existing.mfaEnabled
        );
    }

    // Factory method for enabling/disabling MFA
    public static User withMfaEnabled(User existing, boolean mfaEnabled) {
        return new User(
                existing.id,
                existing.email,
                existing.name,
                existing.passwordHash,
                existing.roles,
                existing.status,
                existing.createdAt,
                OffsetDateTime.now(),
                mfaEnabled
        );
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}