package com.app.auth.application.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAuditLogsQuery {
    private final String userId;
    private final int page;
    private final int size;
}