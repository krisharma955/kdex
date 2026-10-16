package com.k955.kdex.service.impl;

import com.k955.kdex.dtos.Auth.AuthResponse;
import com.k955.kdex.dtos.Auth.LoginRequest;
import com.k955.kdex.dtos.Auth.SignupRequest;
import com.k955.kdex.entity.Transaction;
import com.k955.kdex.entity.User;
import com.k955.kdex.entity.Wallet;
import com.k955.kdex.enums.Transaction.Type;
import com.k955.kdex.exception.BadRequestException;
import com.k955.kdex.exception.ErrorCode;
import com.k955.kdex.exception.ResourceNotFoundException;
import com.k955.kdex.mapper.UserMapper;
import com.k955.kdex.repository.TransactionRepository;
import com.k955.kdex.repository.UserRepository;
import com.k955.kdex.repository.WalletRepository;
import com.k955.kdex.security.JwtAuthUtil;
import com.k955.kdex.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest signupRequest) {
        boolean check = userRepository.existsByEmail(signupRequest.email());
        if(check) throw new BadRequestException(ErrorCode.USER_ALREADY_EXISTS, "An account with this email already exists.");

        User user = User.builder()
                .fullName(signupRequest.fullName())
                .email(signupRequest.email())
                .password(signupRequest.password())
                .build();
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        User savedUser = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .build();
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .user(savedUser)
                .type(Type.SIGNUP_BONUS)
                .amount(BigDecimal.valueOf(1500.0000))
                .balanceAfter(BigDecimal.valueOf(1500.0000))
                .build();
        transactionRepository.save(transaction);

        String token = jwtAuthUtil.generateToken(savedUser);

        return new AuthResponse(token, userMapper.toUserProfileResponse(savedUser));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_CREDENTIALS, "Invalid Email or Password.");
        }

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "User with email: " + loginRequest.email() + " not found."
                ));

        String token = jwtAuthUtil.generateToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }

}
