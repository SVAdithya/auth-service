package com.app.auth.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Builder
@Accessors(fluent = true)
public class UserProfile {
    private final String userId;
    private final String email;
    private final String name;
    private final String phone;
    private final UserAddress address;
    private final UserPreferences preferences;

    // Constructor for creating user profile from user data
    public UserProfile(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = null;
        this.address = null;
        this.preferences = null;
    }

    // Constructor for updating profile with phone
    public UserProfile(UserProfile existing, String phone) {
        this.userId = existing.userId;
        this.email = existing.email;
        this.name = existing.name;
        this.phone = phone;
        this.address = existing.address;
        this.preferences = existing.preferences;
    }

    // Constructor for updating profile with address
    public UserProfile(UserProfile existing, UserAddress address) {
        this.userId = existing.userId;
        this.email = existing.email;
        this.name = existing.name;
        this.phone = existing.phone;
        this.address = address;
        this.preferences = existing.preferences;
    }

    // Constructor for updating profile with preferences
    public UserProfile(UserProfile existing, UserPreferences preferences) {
        this.userId = existing.userId;
        this.email = existing.email;
        this.name = existing.name;
        this.phone = existing.phone;
        this.address = existing.address;
        this.preferences = preferences;
    }

    // Full constructor for complete profile
    public UserProfile(String userId, String email, String name, String phone,
                       UserAddress address, UserPreferences preferences) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.preferences = preferences;
    }

    // Getters
    public String userId() {
        return userId;
    }

    public String email() {
        return email;
    }

    public String name() {
        return name;
    }

    public String phone() {
        return phone;
    }

    public UserAddress address() {
        return address;
    }

    public UserPreferences preferences() {
        return preferences;
    }
}