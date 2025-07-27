package com.app.auth.application.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUsersQuery {
    private final int page;
    private final int size;
}