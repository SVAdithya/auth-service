package com.app.auth.sso.service;

import com.app.auth.sso.config.SsoProvidersConfig;
import com.app.auth.sso.domain.model.SsoMapping;
import com.app.auth.sso.repository.SsoMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SsoService {

    private final SsoMappingRepository ssoMappingRepository;
    private final SsoProvidersConfig providersConfig;

    public List<Map<String, Object>> getAvailableProviders() {
        return providersConfig.getProviders().entrySet().stream()
                .filter(e -> e.getValue().isEnabled())
                .filter(e -> isProviderConfigValid(e.getValue()))
                .map(e -> Map.of(
                        "type", e.getKey().toUpperCase(),
                        "displayName", e.getValue().getDisplayName(),
                        "enabled", true,
                        "scopes", Optional.ofNullable(e.getValue().getScopes()).orElse(Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    public String getAuthorizationUrl(String provider, String redirectUrl) {
        SsoProvidersConfig.ProviderConfig config = mustGetConfig(provider);
        String state = encodeState(redirectUrl);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(config.getAuthorizationUri())
                .queryParam("client_id", config.getClientId())
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("state", state);

        if (config.getScopes() != null && !config.getScopes().isEmpty()) {
            builder.queryParam("scope", String.join(" ", config.getScopes()));
        }

        Optional.ofNullable(config.getAdditionalParams())
                .ifPresent(params -> params.forEach(builder::queryParam));

        String authUrl = builder.build().toUriString();
        log.info("Generated authorization URL for provider {}", provider);
        return authUrl;
    }

    public Map<String, Object> processCallback(String provider, String code, String state) {
        SsoProvidersConfig.ProviderConfig config = mustGetConfig(provider);
        log.info("Processing OAuth callback for provider: {}", provider);

        try {
            String redirectUrl = decodeState(state);
            String accessToken = exchangeCodeForToken(config, code);
            Map<String, Object> userInfo = getUserInfo(config, accessToken);
            String userId = createOrLinkUser(provider, userInfo, accessToken);
            String jwtToken = generateJwtToken(userId);

            return Map.of(
                    "success", true,
                    "token", jwtToken,
                    "userId", userId,
                    "provider", provider,
                    "redirectUrl", redirectUrl,
                    "userInfo", sanitizeUserInfo(userInfo)
            );

        } catch (Exception e) {
            log.error("Failed to process OAuth callback for provider {}: {}", provider, e.getMessage(), e);
            throw new RuntimeException("OAuth callback processing failed", e);
        }
    }

    public boolean unlinkProvider(String userId, String provider) {
        log.info("Unlinking provider {} from user {}", provider, userId);

        Optional<SsoMapping> mapping = ssoMappingRepository.findByUserIdAndProvider(userId, provider);
        if (mapping.isPresent()) {
            mapping.get().deactivate();
            ssoMappingRepository.save(mapping.get());
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getLinkedProviders(String userId) {
        log.info("Getting linked providers for user {}", userId);

        return ssoMappingRepository.findByUserIdAndActive(userId, true).stream()
                .map(mapping -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("provider", mapping.getProvider());
                    result.put("externalEmail", mapping.getExternalEmail());
                    result.put("externalName", mapping.getExternalName());
                    result.put("linkedAt", mapping.getCreatedAt());
                    result.put("lastLoginAt", mapping.getLastLoginAt());
                    return result;
                })
                .collect(Collectors.toList());
    }

    // Token refresh method for cron jobs
    public List<SsoMapping> getTokensExpiringBefore(OffsetDateTime threshold) {
        return ssoMappingRepository.findTokensExpiringBefore(threshold);
    }

    public void updateTokens(SsoMapping mapping, String newAccessToken, String newRefreshToken,
                             OffsetDateTime expiresAt) {
        SsoMapping.TokenInfo tokenInfo = SsoMapping.TokenInfo.builder()
                .accessTokenHash(hashToken(newAccessToken))
                .refreshTokenHash(hashToken(newRefreshToken))
                .accessTokenExpiresAt(expiresAt)
                .refreshTokenExpiresAt(expiresAt.plusDays(30)) // Refresh tokens typically longer-lived
                .scope(mapping.getTokens() != null ? mapping.getTokens().getScope() : "")
                .tokenType("Bearer")
                .build();

        mapping.updateTokens(tokenInfo);
        ssoMappingRepository.save(mapping);
        log.info("Updated tokens for user {} provider {}", mapping.getUserId(), mapping.getProvider());
    }

    private SsoProvidersConfig.ProviderConfig mustGetConfig(String provider) {
        SsoProvidersConfig.ProviderConfig config = providersConfig.getProviders().get(provider.toLowerCase());
        if (config == null || !config.isEnabled() || !isProviderConfigValid(config)) {
            throw new IllegalArgumentException("Invalid or disabled provider: " + provider);
        }
        return config;
    }

    private boolean isProviderConfigValid(SsoProvidersConfig.ProviderConfig config) {
        return config.getClientId() != null && !config.getClientId().trim().isEmpty() &&
                config.getClientSecret() != null && !config.getClientSecret().trim().isEmpty() &&
                config.getAuthorizationUri() != null && !config.getAuthorizationUri().trim().isEmpty() &&
                config.getTokenUri() != null && !config.getTokenUri().trim().isEmpty();
    }

    private String encodeState(String redirectUrl) {
        String stateData = redirectUrl + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID().toString();
        return Base64.getEncoder().encodeToString(stateData.getBytes());
    }

    private String decodeState(String state) {
        try {
            String decoded = new String(Base64.getDecoder().decode(state));
            String[] parts = decoded.split(":", 3);
            return parts.length >= 1 ? parts[0] : "http://localhost:3000/auth/callback/success";
        } catch (Exception e) {
            log.warn("Failed to validate state parameter: {}", e.getMessage());
            return "http://localhost:3000/auth/callback/error";
        }
    }

    private String createOrLinkUser(String provider, Map<String, Object> userInfo, String accessToken) {
        String externalUserId = (String) userInfo.get("id");
        String externalEmail = (String) userInfo.get("email");
        String externalName = (String) userInfo.get("name");

        // Check if mapping already exists
        Optional<SsoMapping> existingMapping = ssoMappingRepository.findByProviderAndExternalUserId(provider, externalUserId);

        if (existingMapping.isPresent()) {
            // Update existing mapping
            SsoMapping mapping = existingMapping.get();
            mapping.setExternalEmail(externalEmail);
            mapping.setExternalName(externalName);
            mapping.setProviderMetadata((Map<String, Object>) userInfo);
            mapping.setLastLoginAt(OffsetDateTime.now());
            mapping.setUpdatedAt(OffsetDateTime.now());
            mapping.setActive(true);

            // Update tokens
            SsoMapping.TokenInfo tokenInfo = SsoMapping.TokenInfo.builder()
                    .accessTokenHash(hashToken(accessToken))
                    .accessTokenExpiresAt(OffsetDateTime.now().plusHours(1))
                    .tokenType("Bearer")
                    .build();
            mapping.setTokens(tokenInfo);

            ssoMappingRepository.save(mapping);
            return mapping.getUserId();
        } else {
            // Create new user and mapping
            String internalUserId = createNewUser(userInfo);

            SsoMapping newMapping = SsoMapping.create(internalUserId, provider, externalUserId, externalEmail, externalName);
            newMapping.setProviderMetadata((Map<String, Object>) userInfo);

            // Set tokens
            SsoMapping.TokenInfo tokenInfo = SsoMapping.TokenInfo.builder()
                    .accessTokenHash(hashToken(accessToken))
                    .accessTokenExpiresAt(OffsetDateTime.now().plusHours(1))
                    .tokenType("Bearer")
                    .build();
            newMapping.setTokens(tokenInfo);

            ssoMappingRepository.save(newMapping);
            return internalUserId;
        }
    }

    private String createNewUser(Map<String, Object> userInfo) {
        // TODO: Integrate with user-service to create new user
        // This should call user-service REST API or use shared database
        log.info("Creating new user from SSO info: {}", userInfo.get("email"));
        return "internal-user-" + UUID.randomUUID().toString();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    private String exchangeCodeForToken(SsoProvidersConfig.ProviderConfig config, String code) {
        // TODO: Implement actual OAuth2 token exchange using WebClient
        log.info("Exchanging authorization code for access token with {}", config.getTokenUri());
        return "mock-access-token-" + UUID.randomUUID().toString();
    }

    private Map<String, Object> getUserInfo(SsoProvidersConfig.ProviderConfig config, String accessToken) {
        // TODO: Implement actual user info retrieval using WebClient
        log.info("Fetching user info from {}", config.getUserInfoUri());
        return Map.of(
                "id", "external-user-" + UUID.randomUUID().toString(),
                "email", "user@example.com",
                "name", "Mock User",
                "avatar_url", "https://example.com/avatar.jpg"
        );
    }

    private String generateJwtToken(String userId) {
        // TODO: Integrate with token-service to generate JWT
        log.info("Generating JWT token for user {}", userId);
        return "jwt-token-" + UUID.randomUUID().toString();
    }

    private Map<String, Object> sanitizeUserInfo(Map<String, Object> userInfo) {
        Map<String, Object> sanitized = new HashMap<>(userInfo);
        sanitized.remove("access_token");
        sanitized.remove("refresh_token");
        return sanitized;
    }
}