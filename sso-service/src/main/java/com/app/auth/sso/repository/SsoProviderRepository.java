package com.app.auth.sso.repository;

import com.app.auth.sso.domain.model.SsoProvider;
import com.app.auth.sso.domain.model.SsoProviderType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SsoProviderRepository extends MongoRepository<SsoProvider, String> {

    List<SsoProvider> findByEnabledTrue();

    Optional<SsoProvider> findByProviderTypeAndEnabledTrue(SsoProviderType providerType);

    Optional<SsoProvider> findByProviderType(SsoProviderType providerType);

    boolean existsByProviderType(SsoProviderType providerType);
}