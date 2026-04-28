package com.buildwithus.user.repository;

import com.buildwithus.common.enums.AuthProvider;
import com.buildwithus.user.entity.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {
    Optional<UserSocialAccount> findByProviderIdAndProvider(String providerId, AuthProvider provider);
    Optional<UserSocialAccount> findByUserIdAndProvider(Long userId, AuthProvider provider);
}