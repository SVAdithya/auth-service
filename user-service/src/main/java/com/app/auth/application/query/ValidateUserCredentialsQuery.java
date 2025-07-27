package com.app.auth.application.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidateUserCredentialsQuery {
    private final String email;
    private final String password;
}