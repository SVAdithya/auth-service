package com.app.auth.controller;

import com.app.auth.application.command.UpdateUserProfileCommand;
import com.app.auth.application.handler.UpdateUserProfileCommandHandler;
import com.app.auth.application.handler.GetUserQueryHandler;
import com.app.auth.application.query.GetUserQuery;
import com.app.auth.domain.model.User;
import com.app.user.api.ProfileManagementApi;
import com.app.user.model.UpdateUserProfileRequest;
import com.app.user.model.UserProfileResponse;
import com.app.user.model.UserProfileResponseAddress;
import com.app.user.model.UserProfileResponsePreferences;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileManagementController implements ProfileManagementApi {

    private final UpdateUserProfileCommandHandler updateUserProfileHandler;
    private final GetUserQueryHandler getUserHandler;

    @Override
    public ResponseEntity<UserProfileResponse> getUserProfile(String id) {
        GetUserQuery query = new GetUserQuery(id);
        User user = getUserHandler.handle(query);

        // TODO: Map from User domain model to UserProfileResponse
        // For now, returning mock data with actual user info
        UserProfileResponseAddress address = new UserProfileResponseAddress()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .postalCode("12345")
                .country("USA");

        UserProfileResponsePreferences preferences = new UserProfileResponsePreferences()
                .emailNotifications(true)
                .smsNotifications(false);

        UserProfileResponse userProfile = new UserProfileResponse()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone("+1-234-567-8900") // TODO: Add phone to User domain model
                .address(address)
                .preferences(preferences);

        return ResponseEntity.ok(userProfile);
    }

    @Override
    public ResponseEntity<UserProfileResponse> updateUserProfile(String id, UpdateUserProfileRequest updateUserProfileRequest) {
        // Get current user data first
        GetUserQuery query = new GetUserQuery(id);
        User currentUser = getUserHandler.handle(query);

        // Create command with available data (keeping current name since request doesn't have name field)
        UpdateUserProfileCommand command = new UpdateUserProfileCommand(
                id,
                currentUser.getName(), // Keep existing name since profile update doesn't change name
                updateUserProfileRequest.getPhone(),
                null, // TODO: Map address when UserAddress domain model is complete
                null  // TODO: Map preferences when UserPreferences domain model is complete
        );

        User updatedUser = updateUserProfileHandler.handle(command);

        // TODO: Map from User domain model to UserProfileResponse properly
        // For now, combining updated user data with request data
        UserProfileResponseAddress address = new UserProfileResponseAddress()
                .street(updateUserProfileRequest.getAddress() != null ?
                        updateUserProfileRequest.getAddress().getStreet() : "123 Main St")
                .city(updateUserProfileRequest.getAddress() != null ?
                        updateUserProfileRequest.getAddress().getCity() : "New York")
                .state(updateUserProfileRequest.getAddress() != null ?
                        updateUserProfileRequest.getAddress().getState() : "NY")
                .postalCode(updateUserProfileRequest.getAddress() != null ?
                        updateUserProfileRequest.getAddress().getPostalCode() : "12345")
                .country(updateUserProfileRequest.getAddress() != null ?
                        updateUserProfileRequest.getAddress().getCountry() : "USA");

        UserProfileResponsePreferences preferences = new UserProfileResponsePreferences()
                .emailNotifications(updateUserProfileRequest.getPreferences() != null ?
                        updateUserProfileRequest.getPreferences().getEmailNotifications() : true)
                .smsNotifications(updateUserProfileRequest.getPreferences() != null ?
                        updateUserProfileRequest.getPreferences().getSmsNotifications() : false);

        UserProfileResponse userProfile = new UserProfileResponse()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .name(updatedUser.getName())
                .phone(updateUserProfileRequest.getPhone() != null ?
                        updateUserProfileRequest.getPhone() : "+1-234-567-8900")
                .address(address)
                .preferences(preferences);

        return ResponseEntity.ok(userProfile);
    }
}