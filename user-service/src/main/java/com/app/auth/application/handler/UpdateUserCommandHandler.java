package com.app.auth.application.handler;

import com.app.auth.application.command.UpdateUserCommand;
import com.app.auth.domain.model.AuditLog;
import com.app.auth.domain.model.User;
import com.app.auth.domain.repository.AuditLogRepository;
import com.app.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpdateUserCommandHandler {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public User handle(UpdateUserCommand command) {
        // Find existing user
        User existingUser = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + command.getUserId()));

        // Create updated user with new name
        User updatedUser = User.withUpdatedName(existingUser, command.getName());

        // Save updated user
        User savedUser = userRepository.save(updatedUser);

        // Create audit log
        AuditLog auditLog = AuditLog.create(
                "USER_UPDATED",
                savedUser.getId(),
                Map.of(
                        "action", "user_update",
                        "previous_name", existingUser.getName(),
                        "new_name", savedUser.getName(),
                        "email", savedUser.getEmail()
                )
        );
        auditLogRepository.save(auditLog);

        return savedUser;
    }
}