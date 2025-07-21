package com.app.auth.controller;

import com.app.auth.controller.model.LoginRequest;
import com.app.auth.controller.model.RefreshRequest;
import com.app.auth.controller.model.TokenResponse;
import com.app.auth.service.AuthService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
public class AuthController implements AuthApi{
    private final AuthService authService;

    @Override
    @RateLimiter(name = "authRateLimiter", fallbackMethod = "loginFallback")
    public ResponseEntity<TokenResponse> authLoginPost(LoginRequest loginRequest) {
        return ResponseEntity.ok(new TokenResponse(
                UUID.randomUUID().toString(),
                UUID.nameUUIDFromBytes(loginRequest.getUsername().getBytes()).toString()  
        ));
    }

    @Override
    @RateLimiter(name = "authRateLimiter", fallbackMethod = "refreshFallback")
    public ResponseEntity<TokenResponse> authRefreshPost(RefreshRequest refreshRequest) {
        return ResponseEntity.ok(new TokenResponse(
                UUID.randomUUID().toString(),
                refreshRequest.getRefreshToken()
        ));
    }

    @Override
    @RateLimiter(name = "authRateLimiter", fallbackMethod = "logoutFallback")
    public ResponseEntity<Void> authLogoutPost() {
        return ResponseEntity.noContent().build();
    }

    // Fallback methods
    public ResponseEntity<TokenResponse> loginFallback(LoginRequest loginRequest, Exception ex) {
        throw new RuntimeException("Too many login attempts. Please try again later.", ex);
    }

    public ResponseEntity<TokenResponse> refreshFallback(RefreshRequest refreshRequest, Exception ex) {
        throw new RuntimeException("Too many refresh attempts. Please try again later.", ex);
    }

    public ResponseEntity<Void> logoutFallback(Exception ex) {
        throw new RuntimeException("Too many logout attempts. Please try again later.", ex);
    }
}
