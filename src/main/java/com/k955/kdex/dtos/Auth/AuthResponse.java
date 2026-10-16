package com.k955.kdex.dtos.Auth;

import com.k955.kdex.dtos.User.UserProfileResponse;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
