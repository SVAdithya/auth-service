package com.app.auth.controller;

import com.app.auth.application.command.CreateUserCommand;
import com.app.auth.application.handler.CreateUserCommandHandler;
import com.app.auth.application.handler.GetUserQueryHandler;
import com.app.auth.application.handler.GetUsersQueryHandler;
import com.app.auth.application.query.GetUserQuery;
import com.app.auth.application.query.GetUsersQuery;
import com.app.auth.domain.model.User;
import com.app.auth.presentation.mapper.UserResponseMapper;
import com.app.user.api.UserManagementApi;
import com.app.user.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserManagementController implements UserManagementApi {

    private final CreateUserCommandHandler createUserHandler;
    private final GetUserQueryHandler getUserHandler;
    private final GetUsersQueryHandler getUsersHandler;
    private final UserResponseMapper userResponseMapper;

    @Override
    public ResponseEntity<UserResponse> createUser(CreateUserRequest createUserRequest) {
        CreateUserCommand command = new CreateUserCommand(
                createUserRequest.getEmail(),
                createUserRequest.getName(),
                createUserRequest.getPassword(),
                createUserRequest.getRoles()
        );

        User user = createUserHandler.handle(command);
        UserResponse response = userResponseMapper.toResponse(user);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(String id) {
        GetUserQuery query = new GetUserQuery(id);

        User user = getUserHandler.handle(query);
        UserResponse response = userResponseMapper.toResponse(user);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserResponse>> listUsers(Integer page, Integer size) {
        GetUsersQuery query = new GetUsersQuery(
                page != null ? page : 0,
                size != null ? size : 10
        );

        List<User> users = getUsersHandler.handle(query);
        List<UserResponse> responses = userResponseMapper.toResponseList(users);

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(String id, UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = new UserResponse()
                .id(id)
                .email(updateUserRequest.getEmail())
                .name(updateUserRequest.getName())
                .roles(updateUserRequest.getRoles())
                .status(UserResponse.StatusEnum.ACTIVE)
                .createdAt(OffsetDateTime.now())
                .mfaEnabled(false);

        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<UserResponse> updateUserPreferences(String id, UpdateUserPreferencesRequest updateUserPreferencesRequest) {
        UserResponse userResponse = new UserResponse()
                .id(id)
                .email("user@example.com")
                .name("John Doe")
                .roles(Arrays.asList("USER"))
                .status(UserResponse.StatusEnum.ACTIVE)
                .createdAt(OffsetDateTime.now())
                .mfaEnabled(false);

        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<UserResponse> updateUserStatus(String id, UpdateUserStatusRequest updateUserStatusRequest) {
        UserResponse userResponse = new UserResponse()
                .id(id)
                .email("user@example.com")
                .name("John Doe")
                .roles(Arrays.asList("USER"))
                .status(UserResponse.StatusEnum.fromValue(updateUserStatusRequest.getStatus().getValue()))
                .createdAt(OffsetDateTime.now())
                .mfaEnabled(false);

        return ResponseEntity.ok(userResponse);
    }
}