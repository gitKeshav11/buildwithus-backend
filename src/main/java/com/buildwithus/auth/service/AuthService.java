package com.buildwithus.auth.service;

import com.buildwithus.auth.dto.*;
import com.buildwithus.user.entity.User;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String refreshToken, Long userId);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void verifyEmail(String token);
    void resendVerificationEmail(String email);
    void changePassword(Long userId, ChangePasswordRequest request);
    AuthResponse processOAuth2User(User user);
}