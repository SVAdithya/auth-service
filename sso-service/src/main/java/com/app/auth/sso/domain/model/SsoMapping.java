package com.app.auth.sso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sso_mappings")
@CompoundIndexes({
        @CompoundIndex(name = "provider_external_user", def = "{'provider': 1, 'externalUserId': 1}", unique = true),
        @CompoundIndex(name = "user_provider", def = "{'userId': 1, 'provider': 1}")
})
public class SsoMapping {

    @Id
    private String id;

    @Indexed
    private String userId; // Internal user ID from user-service

    @Indexed
    private SsoProviderType provider; // e.g., "google", "github", "microsoft"

    private String externalUserId; // Provider's user ID
    private String externalEmail;
    private String externalName;
    private String externalAvatarUrl;

    private TokenInfo tokens;
    private Map<String, Object> providerMetadata; // Flexible provider-specific data

    @Indexed
    private OffsetDateTime lastLoginAt;
    private boolean active = true;

    @Indexed
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenInfo {
        private String accessTokenHash; // SHA-256 hash for security
        private String refreshTokenHash; // SHA-256 hash for security
        private OffsetDateTime accessTokenExpiresAt;
        private OffsetDateTime refreshTokenExpiresAt;
        private String scope;
        private String tokenType = "Bearer";

        // Encrypted tokens stored in cache only, not persisted here
        // Use Redis/memory cache for actual token values
    }

    // Factory method for creating new mappings
    public static SsoMapping create(String userId, String provider, String externalUserId,
                                    String externalEmail, String externalName) {
        OffsetDateTime now = OffsetDateTime.now();
        return SsoMapping.builder()
                .userId(userId)
                .provider(SsoProviderType.valueOf(provider.toUpperCase()))
                .externalUserId(externalUserId)
                .externalEmail(externalEmail)
                .externalName(externalName)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .lastLoginAt(now)
                .build();
    }

    public void updateTokens(TokenInfo tokenInfo) {
        this.tokens = tokenInfo;
        this.updatedAt = OffsetDateTime.now();
        this.lastLoginAt = OffsetDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = OffsetDateTime.now();
    }
}