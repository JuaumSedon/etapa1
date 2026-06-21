package com.exemplo.secrest.dto;

import com.exemplo.secrest.enums.RoleName;

public record UserProfileDto(String name, String email, RoleName role) {
}