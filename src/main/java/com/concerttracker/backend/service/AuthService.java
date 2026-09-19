package com.concerttracker.backend.service;

import com.concerttracker.backend.dto.request.LoginRequest;
import com.concerttracker.backend.dto.request.UserRegisterRequest;
import com.concerttracker.backend.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(UserRegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);
}