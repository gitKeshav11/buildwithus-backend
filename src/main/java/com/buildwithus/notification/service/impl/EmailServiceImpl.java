package com.buildwithus.notification.service.impl;

import com.buildwithus.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.frontend-url}")
    private String frontendUrl;
    
    @Value("${spring.mail.username:noreply@buildwithus.dev}")
    private String fromEmail;
    
    @Override
    @Async
    public void sendVerificationEmail(String to, String token) {
        try {
            String verificationUrl = frontendUrl + "/verify-email?token=" + token;
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Verify your email - Build With Us");
            message.setText("Hello,\n\n" +
                    "Please verify your email by clicking the link below:\n\n" +
                    verificationUrl + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you didn't create an account, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Build With Us Team");
            
            mailSender.send(message);
            log.info("Verification email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}", to, e);
        }
    }
    
    @Override
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            String resetUrl = frontendUrl + "/reset-password?token=" + token;
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Reset your password - Build With Us");
            message.setText("Hello,\n\n" +
                    "You requested to reset your password. Click the link below:\n\n" +
                    resetUrl + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you didn't request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Build With Us Team");
            
            mailSender.send(message);
            log.info("Password reset email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}", to, e);
        }
    }
    
    @Override
    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to Build With Us! 🚀");
            message.setText("Hello " + name + ",\n\n" +
                    "Welcome to Build With Us - the developer ecosystem platform!\n\n" +
                    "Here's what you can do:\n" +
                    "• Create your developer profile\n" +
                    "• Post and discover projects\n" +
                    "• Find collaborators\n" +
                    "• Browse jobs and internships\n" +
                    "• Use AI-powered code review\n" +
                    "• Join hackathon teams\n" +
                    "• Earn badges and climb the leaderboard\n\n" +
                    "Start by completing your profile!\n\n" +
                    "Best regards,\n" +
                    "Build With Us Team");
            
            mailSender.send(message);
            log.info("Welcome email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}", to, e);
        }
    }
}