package com.app.auth.sso.repository;

import com.app.auth.sso.domain.model.SsoUserMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SsoUserMappingRepository extends MongoRepository<SsoUserMapping, String> {

    List<SsoUserMapping> findByInternalUserId(String internalUserId);

    List<SsoUserMapping> findByInternalUserIdAndActiveTrue(String internalUserId);

    Optional<SsoUserMapping> findByProviderIdAndExternalUserId(String providerId, String externalUserId);

    Optional<SsoUserMapping> findByInternalUserIdAndProviderId(String internalUserId, String providerId);

    boolean existsByInternalUserIdAndProviderId(String internalUserId, String providerId);

    List<SsoUserMapping> findByExternalEmail(String externalEmail);
}