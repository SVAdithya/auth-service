package com.app.auth.sso.repository;

import com.app.auth.sso.domain.model.SsoMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SsoMappingRepository extends MongoRepository<SsoMapping, String> {

    List<SsoMapping> findByUserIdAndActive(String userId, boolean active);

    Optional<SsoMapping> findByProviderAndExternalUserId(String provider, String externalUserId);

    Optional<SsoMapping> findByUserIdAndProvider(String userId, String provider);

    List<SsoMapping> findByUserIdAndProviderAndActive(String userId, String provider, boolean active);

    // Find tokens expiring soon for cron job refresh
    @Query("{'tokens.accessTokenExpiresAt': {$lt: ?0}, 'active': true}")
    List<SsoMapping> findTokensExpiringBefore(OffsetDateTime threshold);

    // Find mappings by provider for bulk operations
    List<SsoMapping> findByProviderAndActive(String provider, boolean active);

    // Find by external email for user lookup
    List<SsoMapping> findByExternalEmailAndActive(String externalEmail, boolean active);

    // Count active mappings per user
    long countByUserIdAndActive(String userId, boolean active);

    // Find inactive mappings for cleanup
    List<SsoMapping> findByActiveAndUpdatedAtBefore(boolean active, OffsetDateTime cutoffDate);
}