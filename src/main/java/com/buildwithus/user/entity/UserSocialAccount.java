package com.buildwithus.user.entity;

import com.buildwithus.common.entity.BaseEntity;
import com.buildwithus.common.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_social_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSocialAccount extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;
    
    @Column(name = "provider_id", nullable = false)
    private String providerId;
    
    @Column(name = "provider_email")
    private String providerEmail;
    
    @Column(name = "provider_username")
    private String providerUsername;
    
    @Column(name = "provider_avatar_url")
    private String providerAvatarUrl;
    
    @Column(name = "access_token", length = 2000)
    private String accessToken;
    
    @Column(name = "refresh_token", length = 2000)
    private String refreshToken;
}