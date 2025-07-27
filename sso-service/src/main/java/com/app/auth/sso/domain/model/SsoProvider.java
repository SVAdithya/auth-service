package com.app.auth.sso.domain.model;

import com.app.auth.sso.domain.model.SsoProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document(collection = "sso_providers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SsoProvider {

    @Id
    private String id;

    private SsoProviderType providerType;

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String scope;
    private boolean enabled = true;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}