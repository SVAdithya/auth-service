package com.app.auth.application.command;

import com.app.auth.domain.model.UserAddress;
import com.app.auth.domain.model.UserPreferences;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserProfileCommand {
    private final String userId;
    private final String name;
    private final String phone;
    private final UserAddress address;
    private final UserPreferences preferences;
}