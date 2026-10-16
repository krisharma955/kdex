package com.k955.kdex.dtos.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
