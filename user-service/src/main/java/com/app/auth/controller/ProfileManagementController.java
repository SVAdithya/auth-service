package com.app.auth.controller;

import com.app.user.api.ProfileManagementApi;
import com.app.user.model.UpdateUserProfileRequest;
import com.app.user.model.UserProfileResponse;
import com.app.user.model.UserProfileResponseAddress;
import com.app.user.model.UserProfileResponsePreferences;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileManagementController implements ProfileManagementApi {

    @Override
    public ResponseEntity<UserProfileResponse> getUserProfile(String id) {
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
                .id(id)
                .email("user@example.com")
                .name("John Doe")
                .phone("+1-234-567-8900")
                .address(address)
                .preferences(preferences);

        return ResponseEntity.ok(userProfile);
    }

    @Override
    public ResponseEntity<UserProfileResponse> updateUserProfile(String id, UpdateUserProfileRequest updateUserProfileRequest) {
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
                .id(id)
                .email("user@example.com")
                .name("John Doe")
                .phone(updateUserProfileRequest.getPhone() != null ?
                        updateUserProfileRequest.getPhone() : "+1-234-567-8900")
                .address(address)
                .preferences(preferences);

        return ResponseEntity.ok(userProfile);
    }
}