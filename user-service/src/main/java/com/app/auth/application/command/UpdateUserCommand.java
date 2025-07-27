package com.app.auth.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateUserCommand {
    private final String userId;
    private final String email;
    private final String name;
    private final List<String> roles;
}