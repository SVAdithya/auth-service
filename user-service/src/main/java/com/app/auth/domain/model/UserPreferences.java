package com.app.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    private boolean emailNotifications = true;
    private boolean smsNotifications = false;
}