package com.greatleyposhley.professy.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE,
    FEMALE;

    @JsonCreator
    public static Gender fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Gender.valueOf(value.trim().toUpperCase());
    }
}