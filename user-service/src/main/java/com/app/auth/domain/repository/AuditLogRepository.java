package com.app.auth.domain.repository;

import com.app.auth.domain.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    Page<AuditLog> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    List<AuditLog> findByEventType(String eventType);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.timestamp >= :fromDate ORDER BY a.timestamp DESC")
    List<AuditLog> findByUserIdAndTimestampAfter(@Param("userId") String userId, @Param("fromDate") OffsetDateTime fromDate);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.eventType = :eventType")
    long countByEventType(@Param("eventType") String eventType);
}