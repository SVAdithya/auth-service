package com.app.auth.sso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document(collection = "sso_user_mappings")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SsoUserMapping {

    @Id
    private String id;

    @Indexed
    private String providerId;

    @Indexed
    private String externalUserId;

    @Indexed
    private String internalUserId;

    private String externalEmail;
    private String externalName;
    private String externalAvatarUrl;
    private String accessTokenHash;
    private String refreshTokenHash;
    private OffsetDateTime tokenExpiresAt;
    private OffsetDateTime lastLoginAt;
    private boolean active = true;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static SsoUserMapping create(String providerId, String externalUserId,
                                        String internalUserId, String externalEmail,
                                        String externalName) {
        OffsetDateTime now = OffsetDateTime.now();
        return SsoUserMapping.builder()
                .providerId(providerId)
                .externalUserId(externalUserId)
                .internalUserId(internalUserId)
                .externalEmail(externalEmail)
                .externalName(externalName)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .lastLoginAt(now)
                .build();
    }

    public void updateTimestamps() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }
}