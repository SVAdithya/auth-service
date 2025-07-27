package com.app.auth.application.handler;

import com.app.auth.application.query.GetUserQuery;
import com.app.auth.domain.model.User;
import com.app.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserQueryHandler {

    private final UserRepository userRepository;

    public User handle(GetUserQuery query) {
        return userRepository.findById(query.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + query.getUserId()));
    }
}