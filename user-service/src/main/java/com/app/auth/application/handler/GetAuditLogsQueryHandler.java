package com.app.auth.application.handler;

import com.app.auth.application.query.GetAuditLogsQuery;
import com.app.auth.domain.model.AuditLog;
import com.app.auth.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAuditLogsQueryHandler {

    private final AuditLogRepository auditLogRepository;

    public List<AuditLog> handle(GetAuditLogsQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        return auditLogRepository.findByUserIdOrderByTimestampDesc(query.getUserId(), pageable).getContent();
    }
}