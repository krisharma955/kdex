package com.k955.kdex.dtos.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank
        @Size(max = 255, message = "Full name can have at most 255 characters")
        String fullName,

        @NotBlank
        @Email
        @Size(max = 255, message = "Email can have at most 255 characters")
        String email,

        @NotBlank
        String password
) {
}
