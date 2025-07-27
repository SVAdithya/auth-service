package com.app.auth.application.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserQuery {
    private final String userId;
}