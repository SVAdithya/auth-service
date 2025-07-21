package com.app.auth.controller;

import com.app.auth.controller.model.LoginRequest;
import com.app.auth.controller.model.RefreshRequest;
import com.app.auth.controller.model.TokenResponse;
import com.app.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
public class AuthController implements AuthApi{
    private final AuthService authService;

    @Override
    public ResponseEntity<TokenResponse> authLoginPost(LoginRequest loginRequest) {
        return ResponseEntity.ok(new TokenResponse(
                UUID.randomUUID().toString(),
                UUID.nameUUIDFromBytes(loginRequest.getUsername().getBytes()).toString()  
        ));
    }

    @Override
    public ResponseEntity<TokenResponse> authRefreshPost(RefreshRequest refreshRequest) {
        return ResponseEntity.ok(new TokenResponse(
                UUID.randomUUID().toString(),
                refreshRequest.getRefreshToken()
        ));
    }

    @Override
    public ResponseEntity<Void> authLogoutPost() {
        return ResponseEntity.noContent().build();
    }
}
