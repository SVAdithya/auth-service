package com.app.auth.controller;

import com.app.user.api.AuthenticationApi;
import com.app.user.model.LoginRequest;
import com.app.user.model.LoginResponse;
import com.app.user.model.PasswordResetRequest;
import com.app.user.model.ResetPasswordRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController implements AuthenticationApi {

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        // TODO: Implement credential validation logic
        // This should only validate username/password
        // Token generation happens in token-service

        // For now, returning basic response without token
        // In real implementation, this would validate credentials and return user info
        LoginResponse loginResponse = new LoginResponse()
                .requiresMfa(false); // No token field populated

        return ResponseEntity.ok(loginResponse);
    }

    @Override
    public ResponseEntity<Void> requestPasswordReset(PasswordResetRequest passwordResetRequest) {
        // TODO: Implement password reset request logic
        // Generate reset token and send email
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<Void> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // TODO: Implement password reset logic
        // Validate reset token and update password
        return ResponseEntity.ok().build();
    }
}