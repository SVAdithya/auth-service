package com.app.auth.sso.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Data
public class SsoProvidersConfig {
    @Value("${sso.providers.config:{}}")
    private String providersJson;

    private Map<String, ProviderConfig> providers = new HashMap<>();

    @Data
    public static class ProviderConfig {
        private boolean enabled = false;
        private String displayName;
        private String clientId;
        private String clientSecret;
        private List<String> scopes;
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String redirectUri;
        private Map<String, String> additionalParams = new HashMap<>();
        private Map<String, Object> metadata = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        if (providersJson == null || providersJson.trim().isEmpty() || providersJson.trim().equals("{}")) {
            log.warn("No SSO providers loaded (empty config)");
            return;
        }
        try {
            String parsed = providersJson.replaceAll("\\\\\"(\\w+)\\\\\":", "\"$1\":"); // Compatibility: Remove extra backslashes from JSON property names
            Map<String, ProviderConfig> map = new ObjectMapper().readValue(parsed,
                    new TypeReference<Map<String, ProviderConfig>>() {
                    });
            providers = map;
            log.info("Loaded SSO Providers config: {}", providers.keySet());
        } catch (Exception e) {
            log.error("Failed to load SSO providers config!", e);
            throw new IllegalStateException("Cannot parse SSO providers config property", e);
        }
    }
}