package com.app.auth.application.handler;

import com.app.auth.application.query.GetUsersQuery;
import com.app.auth.domain.model.User;
import com.app.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUsersQueryHandler {

    private final UserRepository userRepository;

    public List<User> handle(GetUsersQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        return userRepository.findAll(pageable).getContent();
    }
}