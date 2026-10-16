package com.k955.kdex.service;

import com.k955.kdex.dtos.Auth.AuthResponse;
import com.k955.kdex.dtos.Auth.LoginRequest;
import com.k955.kdex.dtos.Auth.SignupRequest;
import jakarta.validation.Valid;

public interface AuthService {

    AuthResponse signup(@Valid SignupRequest signupRequest);

    AuthResponse login(@Valid LoginRequest loginRequest);

}
