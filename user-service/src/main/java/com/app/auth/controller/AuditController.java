package com.app.auth.controller;

import com.app.auth.application.handler.GetAuditLogsQueryHandler;
import com.app.auth.application.query.GetAuditLogsQuery;
import com.app.auth.domain.model.AuditLog;
import com.app.user.api.AuditApi;
import com.app.user.model.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuditController implements AuditApi {

    private final GetAuditLogsQueryHandler getAuditLogsHandler;

    @Override
    public ResponseEntity<List<AuditLogResponse>> getUserAuditLogs(String id, Integer page, Integer size) {
        GetAuditLogsQuery query = new GetAuditLogsQuery(
                id,
                page != null ? page : 0,
                size != null ? size : 10
        );

        List<AuditLog> auditLogs = getAuditLogsHandler.handle(query);

        List<AuditLogResponse> responses = auditLogs.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {
        return new AuditLogResponse()
                .eventId(auditLog.getEventId())
                .eventType(auditLog.getEventType())
                .userId(auditLog.getUserId())
                .timestamp(auditLog.getTimestamp())
                .details(auditLog.getDetails());
    }
}