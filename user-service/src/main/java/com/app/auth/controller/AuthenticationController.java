package com.app.auth.controller;

import com.app.auth.application.handler.ValidateUserCredentialsQueryHandler;
import com.app.auth.application.query.ValidateUserCredentialsQuery;
import com.app.user.api.AuthenticationApi;
import com.app.user.model.LoginRequest;
import com.app.user.model.LoginResponse;
import com.app.user.model.PasswordResetRequest;
import com.app.user.model.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationApi {

    private final ValidateUserCredentialsQueryHandler validateCredentialsHandler;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        // Validate user credentials
        ValidateUserCredentialsQuery query = new ValidateUserCredentialsQuery(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        boolean credentialsValid = validateCredentialsHandler.handle(query);

        if (!credentialsValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Credentials are valid - return login response
        // Token generation should happen in token-service
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