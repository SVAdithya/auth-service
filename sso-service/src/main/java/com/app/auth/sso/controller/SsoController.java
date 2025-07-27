package com.app.auth.sso.controller;

import com.app.auth.sso.service.SsoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/sso")
@RequiredArgsConstructor
@Slf4j
public class SsoController {

    private final SsoService ssoService;

    /**
     * Initiate OAuth flow with specified provider
     */
    @GetMapping("/login/{provider}")
    public ResponseEntity<?> initiateLogin(@PathVariable String provider,
                                           @RequestParam(required = false) String redirectUrl) {
        try {
            String authorizationUrl = ssoService.getAuthorizationUrl(provider, redirectUrl);

            return ResponseEntity.ok(Map.of(
                    "authorizationUrl", authorizationUrl,
                    "provider", provider
            ));

        } catch (Exception e) {
            log.error("Failed to initiate SSO login for provider: {}", provider, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "invalid_provider",
                    "message", "Unsupported or disabled SSO provider: " + provider
            ));
        }
    }

    /**
     * Handle OAuth callback from external provider
     */
    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> handleCallback(@PathVariable String provider,
                                            @RequestParam String code,
                                            @RequestParam(required = false) String state,
                                            @RequestParam(required = false) String error) {
        try {
            if (error != null) {
                log.warn("OAuth error for provider {}: {}", provider, error);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "oauth_error",
                        "message", "Authentication failed: " + error
                ));
            }

            // Process the OAuth callback
            Map<String, Object> result = ssoService.processCallback(provider, code, state);

            // Redirect to frontend with authentication result
            String redirectUrl = (String) result.get("redirectUrl");
            if (redirectUrl != null) {
                return ResponseEntity.status(302)
                        .location(URI.create(redirectUrl))
                        .build();
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Failed to process SSO callback for provider: {}", provider, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "callback_error",
                    "message", "Failed to process authentication callback"
            ));
        }
    }

    /**
     * Get available SSO providers
     */
    @GetMapping("/providers")
    public ResponseEntity<?> getProviders() {
        try {
            return ResponseEntity.ok(ssoService.getAvailableProviders());
        } catch (Exception e) {
            log.error("Failed to get SSO providers", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "internal_error",
                    "message", "Failed to retrieve SSO providers"
            ));
        }
    }

    /**
     * Unlink SSO provider from user account
     */
    @DeleteMapping("/unlink/{provider}")
    public ResponseEntity<?> unlinkProvider(@PathVariable String provider,
                                            @RequestHeader("Authorization") String token) {
        try {
            // Extract user ID from token (this would integrate with token-service)
            String userId = extractUserIdFromToken(token);

            boolean unlinked = ssoService.unlinkProvider(userId, provider);

            if (unlinked) {
                return ResponseEntity.ok(Map.of(
                        "message", "Provider unlinked successfully",
                        "provider", provider
                ));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("Failed to unlink SSO provider: {}", provider, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "unlink_error",
                    "message", "Failed to unlink provider"
            ));
        }
    }

    /**
     * Get user's linked SSO providers
     */
    @GetMapping("/linked")
    public ResponseEntity<?> getLinkedProviders(@RequestHeader("Authorization") String token) {
        try {
            String userId = extractUserIdFromToken(token);
            return ResponseEntity.ok(ssoService.getLinkedProviders(userId));

        } catch (Exception e) {
            log.error("Failed to get linked providers", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "fetch_error",
                    "message", "Failed to retrieve linked providers"
            ));
        }
    }

    private String extractUserIdFromToken(String token) {
        // TODO: Integrate with token-service to validate and extract user ID
        // For now, returning placeholder
        return "user-id-from-token";
    }
}