package com.app.auth.application.command;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUserCommand {
    private final String email;
    private final String name;
    private final String password;
    private final List<String> roles;
}