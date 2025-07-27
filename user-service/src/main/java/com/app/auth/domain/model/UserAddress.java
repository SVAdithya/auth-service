package com.app.auth.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}