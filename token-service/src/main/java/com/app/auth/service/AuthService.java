package com.app.auth.service;

import com.app.auth.controller.model.TokenResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    public TokenResponse login(@NotNull String username) {
        return new TokenResponse(
                UUID.randomUUID().toString(),
                UUID.nameUUIDFromBytes(username.getBytes()).toString()
        );
    }
}
