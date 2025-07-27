package com.app.auth.application.handler;

import com.app.auth.application.command.CreateUserCommand;
import com.app.auth.domain.model.AuditLog;
import com.app.auth.domain.model.User;
import com.app.auth.domain.repository.AuditLogRepository;
import com.app.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateUserCommandHandler {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public User handle(CreateUserCommand command) {
        // For now, using plain text password - will add BCrypt later
        User user = User.create(
                command.getEmail(),
                command.getName(),
                command.getPassword(), // TODO: Add password encoding
                command.getRoles()
        );

        User savedUser = userRepository.save(user);

        AuditLog auditLog = AuditLog.create(
                "USER_CREATED",
                savedUser.getId(),
                Map.of(
                        "action", "user_creation",
                        "email", savedUser.getEmail()
                )
        );
        auditLogRepository.save(auditLog);

        return savedUser;
    }
}