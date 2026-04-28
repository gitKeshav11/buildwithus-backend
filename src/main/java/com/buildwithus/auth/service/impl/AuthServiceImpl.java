package com.buildwithus.auth.service.impl;

import com.buildwithus.auth.dto.*;
import com.buildwithus.auth.entity.EmailVerificationToken;
import com.buildwithus.auth.entity.PasswordResetToken;
import com.buildwithus.auth.entity.RefreshToken;
import com.buildwithus.auth.repository.EmailVerificationTokenRepository;
import com.buildwithus.auth.repository.PasswordResetTokenRepository;
import com.buildwithus.auth.repository.RefreshTokenRepository;
import com.buildwithus.auth.service.AuthService;
import com.buildwithus.common.enums.AuthProvider;
import com.buildwithus.exception.BadRequestException;
import com.buildwithus.exception.ResourceNotFoundException;
import com.buildwithus.exception.UnauthorizedException;
import com.buildwithus.notification.service.EmailService;
import com.buildwithus.profile.entity.DeveloperProfile;
import com.buildwithus.profile.repository.DeveloperProfileRepository;
import com.buildwithus.security.JwtTokenProvider;
import com.buildwithus.user.entity.Role;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.repository.RoleRepository;
import com.buildwithus.user.repository.UserRepository;
import com.buildwithus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final DeveloperProfileRepository developerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserService userService;
    
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;
    
    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        
        String username = request.getUsername();
        if (username == null || username.isBlank()) {
            username = generateUsernameFromEmail(request.getEmail());
        }
        
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username is already taken");
        }
        
        Role userRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .username(username)
                .authProvider(AuthProvider.LOCAL)
                .emailVerified(false)
                .isActive(true)
                .isBlocked(false)
                .build();
        user.addRole(userRole);
        
        user = userRepository.save(user);
        
        DeveloperProfile profile = DeveloperProfile.builder()
                .user(user)
                .fullName(request.getFullName())
                .username(username)
                .email(request.getEmail())
                .profileCompletionPercentage(10)
                .build();
        developerProfileRepository.save(profile);
        
        sendVerificationEmail(user);
        
        return generateAuthResponse(user);
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        if (user.getIsBlocked()) {
            throw new UnauthorizedException("Your account has been blocked. Please contact support.");
        }
        
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Your account is inactive. Please contact support.");
        }
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        return generateAuthResponse(user);
    }
    
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        
        if (refreshToken.getIsRevoked() || refreshToken.isExpired()) {
            throw new UnauthorizedException("Refresh token is expired or revoked");
        }
        
        User user = refreshToken.getUser();
        
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);
        
        return generateAuthResponse(user);
    }
    
    @Override
    public void logout(String refreshToken, Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
    
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        
        if (user != null) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                    .isUsed(false)
                    .build();
            passwordResetTokenRepository.save(resetToken);
            
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        }
    }
    
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));
        
        if (resetToken.getIsUsed() || resetToken.isExpired()) {
            throw new BadRequestException("Reset token is expired or already used");
        }
        
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        resetToken.setIsUsed(true);
        passwordResetTokenRepository.save(resetToken);
        
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }
    
    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));
        
        if (verificationToken.getIsUsed() || verificationToken.isExpired()) {
            throw new BadRequestException("Verification token is expired or already used");
        }
        
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        
        verificationToken.setIsUsed(true);
        emailVerificationTokenRepository.save(verificationToken);
    }
    
    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        if (user.getEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }
        
        sendVerificationEmail(user);
    }
    
    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        refreshTokenRepository.revokeAllByUserId(userId);
    }
    
    @Override
    public AuthResponse processOAuth2User(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        return generateAuthResponse(user);
    }
    
    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000)
                .user(userService.toDTO(user))
                .build();
    }
    
    private String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .isRevoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }
    
    private void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .isUsed(false)
                .build();
        emailVerificationTokenRepository.save(verificationToken);
        
        emailService.sendVerificationEmail(user.getEmail(), token);
    }
    
    private String generateUsernameFromEmail(String email) {
        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9_-]", "");
        String username = base;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + counter++;
        }
        return username;
    }
}