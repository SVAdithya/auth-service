package com.app.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAddress {
    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;
}