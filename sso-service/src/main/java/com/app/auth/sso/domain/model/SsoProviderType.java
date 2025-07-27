package com.app.auth.sso.domain.model;

public enum SsoProviderType {
    GOOGLE("Google"),
    GITHUB("GitHub"),
    MICROSOFT("Microsoft"),
    FACEBOOK("Facebook"),
    TWITTER("Twitter"),
    LINKEDIN("LinkedIn"),
    APPLE("Apple"),
    OKTA("Okta"),
    AUTH0("Auth0"),
    CUSTOM("Custom");

    private final String displayName;

    SsoProviderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
