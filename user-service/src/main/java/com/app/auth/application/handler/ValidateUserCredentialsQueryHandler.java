package com.app.auth.application.handler;

import com.app.auth.application.query.ValidateUserCredentialsQuery;
import com.app.auth.domain.model.User;
import com.app.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateUserCredentialsQueryHandler {

    private final UserRepository userRepository;

    public boolean handle(ValidateUserCredentialsQuery query) {
        return userRepository.findByEmail(query.getEmail())
                .map(user -> validatePassword(query.getPassword(), user.getPasswordHash()))
                .orElse(false);
    }

    private boolean validatePassword(String plainPassword, String hashedPassword) {
        // TODO: Implement BCrypt password verification
        // For now, using simple string comparison
        return plainPassword.equals(hashedPassword);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}