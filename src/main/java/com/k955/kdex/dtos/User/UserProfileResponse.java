package com.k955.kdex.dtos.User;

import com.k955.kdex.enums.User.Role;
import com.k955.kdex.enums.User.Status;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String fullName,
        String email,
        Role role,
        Status status,
        Instant createdAt
) {
}
