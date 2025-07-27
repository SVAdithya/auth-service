package com.app.auth.presentation.mapper;

import com.app.auth.domain.model.User;
import com.app.user.model.UserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserResponseMapper {

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();

        // Copy matching properties automatically
        BeanUtils.copyProperties(user, response, "status");

        // Handle custom mappings
        response.status(UserResponse.StatusEnum.fromValue(user.getStatus().name()));

        return response;
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}