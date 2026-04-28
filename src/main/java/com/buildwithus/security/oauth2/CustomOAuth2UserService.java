package com.buildwithus.security.oauth2;

import com.buildwithus.common.enums.AuthProvider;
import com.buildwithus.exception.OAuth2AuthenticationProcessingException;
import com.buildwithus.profile.entity.DeveloperProfile;
import com.buildwithus.profile.repository.DeveloperProfileRepository;
import com.buildwithus.security.UserPrincipal;
import com.buildwithus.user.entity.Role;
import com.buildwithus.user.entity.User;
import com.buildwithus.user.entity.UserSocialAccount;
import com.buildwithus.user.repository.RoleRepository;
import com.buildwithus.user.repository.UserRepository;
import com.buildwithus.user.repository.UserSocialAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserSocialAccountRepository socialAccountRepository;
    private final DeveloperProfileRepository developerProfileRepository;
    private final WebClient webClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            Map<String, Object> enrichedAttributes = new HashMap<>(oauth2User.getAttributes());
            enrichProviderAttributes(registrationId, userRequest, enrichedAttributes);

            String userNameAttributeName = userRequest.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();

            OAuth2User enrichedUser = new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    enrichedAttributes,
                    StringUtils.hasText(userNameAttributeName) ? userNameAttributeName : "id"
            );

            return processOAuth2User(userRequest, enrichedUser);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);

            throw new OAuth2AuthenticationException(
                    new org.springframework.security.oauth2.core.OAuth2Error(
                            "invalid_oauth2_user",
                            ex.getMessage(),
                            null
                    )
            );
        }
    }

    private void enrichProviderAttributes(String registrationId, OAuth2UserRequest userRequest,
                                          Map<String, Object> attributes) {
        if ("github".equalsIgnoreCase(registrationId)) {
            enrichGithubAttributes(userRequest, attributes);
        } else if ("linkedin".equalsIgnoreCase(registrationId)) {
            enrichLinkedInAttributes(userRequest, attributes);
        }
    }

    private void enrichGithubAttributes(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
        String accessToken = userRequest.getAccessToken().getTokenValue();

        if (!StringUtils.hasText((String) attributes.get("email"))) {
            try {
                List<Map<String, Object>> emails = webClient.get()
                        .uri("https://api.github.com/user/emails")
                        .headers(headers -> {
                            headers.setBearerAuth(accessToken);
                            headers.set("Accept", "application/vnd.github+json");
                        })
                        .retrieve()
                        .bodyToMono(List.class)
                        .block();

                String resolvedEmail = extractPrimaryGithubEmail(emails);
                if (StringUtils.hasText(resolvedEmail)) {
                    attributes.put("email", resolvedEmail);
                }
            } catch (Exception ex) {
                log.warn("Unable to fetch GitHub email: {}", ex.getMessage());
            }
        }

        if (!StringUtils.hasText((String) attributes.get("name"))) {
            attributes.put("name", attributes.get("login"));
        }
    }

    private String extractPrimaryGithubEmail(List<Map<String, Object>> emails) {
        if (emails == null || emails.isEmpty()) {
            return null;
        }

        return emails.stream()
                .filter(email -> Boolean.TRUE.equals(email.get("verified")))
                .sorted(Comparator.comparing(email -> !Boolean.TRUE.equals(email.get("primary"))))
                .map(email -> (String) email.get("email"))
                .filter(StringUtils::hasText)
                .findFirst()
                .orElseGet(() -> emails.stream()
                        .map(email -> (String) email.get("email"))
                        .filter(StringUtils::hasText)
                        .findFirst()
                        .orElse(null));
    }

    private void enrichLinkedInAttributes(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
        String accessToken = userRequest.getAccessToken().getTokenValue();

        if (!StringUtils.hasText((String) attributes.get("email"))) {
            try {
                Map<String, Object> emailResponse = webClient.get()
                        .uri("https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))")
                        .headers(headers -> headers.setBearerAuth(accessToken))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                String email = extractLinkedInEmail(emailResponse);
                if (StringUtils.hasText(email)) {
                    attributes.put("email", email);
                }
            } catch (Exception ex) {
                log.warn("Unable to fetch LinkedIn email: {}", ex.getMessage());
            }
        }

        if (!StringUtils.hasText((String) attributes.get("imageUrl"))) {
            try {
                Map<String, Object> profileResponse = webClient.get()
                        .uri("https://api.linkedin.com/v2/me?projection=(id,localizedFirstName,localizedLastName,profilePicture(displayImage~:playableStreams))")
                        .headers(headers -> headers.setBearerAuth(accessToken))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                if (profileResponse != null) {
                    profileResponse.forEach(attributes::putIfAbsent);
                    String imageUrl = extractLinkedInProfileImage(profileResponse);
                    if (StringUtils.hasText(imageUrl)) {
                        attributes.put("imageUrl", imageUrl);
                    }
                }
            } catch (Exception ex) {
                log.warn("Unable to fetch LinkedIn profile image: {}", ex.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private String extractLinkedInEmail(Map<String, Object> emailResponse) {
        if (emailResponse == null) {
            return null;
        }

        Object elementsObj = emailResponse.get("elements");
        if (!(elementsObj instanceof List<?> elements) || elements.isEmpty()) {
            return null;
        }

        Object firstElement = elements.get(0);
        if (!(firstElement instanceof Map<?, ?> firstMap)) {
            return null;
        }

        Object handleObj = firstMap.get("handle~");
        if (!(handleObj instanceof Map<?, ?> handleMap)) {
            return null;
        }

        Object email = handleMap.get("emailAddress");
        return email != null ? String.valueOf(email) : null;
    }

    @SuppressWarnings("unchecked")
    private String extractLinkedInProfileImage(Map<String, Object> profileResponse) {
        Object profilePictureObj = profileResponse.get("profilePicture");
        if (!(profilePictureObj instanceof Map<?, ?> profilePictureMap)) {
            return null;
        }

        Object displayImageObj = profilePictureMap.get("displayImage~");
        if (!(displayImageObj instanceof Map<?, ?> displayImageMap)) {
            return null;
        }

        Object elementsObj = displayImageMap.get("elements");
        if (!(elementsObj instanceof List<?> elements) || elements.isEmpty()) {
            return null;
        }

        for (int i = elements.size() - 1; i >= 0; i--) {
            Object elementObj = elements.get(i);
            if (!(elementObj instanceof Map<?, ?> elementMap)) {
                continue;
            }

            Object identifiersObj = elementMap.get("identifiers");
            if (!(identifiersObj instanceof List<?> identifiers) || identifiers.isEmpty()) {
                continue;
            }

            Object identifierObj = identifiers.get(0);
            if (identifierObj instanceof Map<?, ?> identifierMap) {
                Object identifier = identifierMap.get("identifier");
                if (identifier != null) {
                    return String.valueOf(identifier);
                }
            }
        }

        return null;
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oauth2User.getAttributes());

        String email = userInfo.getEmail();

        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationProcessingException(
                    "Email not found from OAuth2 provider. Please make sure email permission is granted."
            );
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            updateUserFromOAuth(user, provider, userInfo, email);
            updateOrCreateSocialAccount(user, provider, userInfo, userRequest, email);
        } else {
            user = registerNewUser(provider, userInfo, userRequest, email);
        }

        return UserPrincipal.create(user, oauth2User.getAttributes());
    }

    private User registerNewUser(AuthProvider provider, OAuth2UserInfo userInfo,
                                 OAuth2UserRequest userRequest, String email) {
        Role userRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        String username = generateUsername(userInfo.getName(), email);

        User user = User.builder()
                .email(email)
                .fullName(userInfo.getName())
                .username(username)
                .authProvider(provider)
                .providerId(userInfo.getId())
                .emailVerified(true)
                .isActive(true)
                .isBlocked(false)
                .build();
        user.addRole(userRole);

        user = userRepository.save(user);

        DeveloperProfile profile = DeveloperProfile.builder()
                .user(user)
                .fullName(userInfo.getName())
                .username(username)
                .email(email)
                .profilePhotoUrl(userInfo.getImageUrl())
                .profileCompletionPercentage(20)
                .build();
        developerProfileRepository.save(profile);

        createSocialAccount(user, provider, userInfo, userRequest, email);

        return user;
    }

    private void updateUserFromOAuth(User user, AuthProvider provider, OAuth2UserInfo userInfo, String email) {
        user.setAuthProvider(provider);
        if (StringUtils.hasText(userInfo.getId())) {
            user.setProviderId(userInfo.getId());
        }
        if (StringUtils.hasText(userInfo.getName())) {
            user.setFullName(userInfo.getName());
        }
        user.setEmailVerified(true);
        userRepository.save(user);

        developerProfileRepository.findByUserId(user.getId()).ifPresent(profile -> {
            if (StringUtils.hasText(userInfo.getName())) {
                profile.setFullName(userInfo.getName());
            }
            if (StringUtils.hasText(email)) {
                profile.setEmail(email);
            }
            if (StringUtils.hasText(userInfo.getImageUrl())) {
                profile.setProfilePhotoUrl(userInfo.getImageUrl());
            }
            developerProfileRepository.save(profile);
        });
    }

    private void updateOrCreateSocialAccount(User user, AuthProvider provider, OAuth2UserInfo userInfo,
                                             OAuth2UserRequest userRequest, String email) {
        Optional<UserSocialAccount> existingAccount = socialAccountRepository
                .findByUserIdAndProvider(user.getId(), provider);

        if (existingAccount.isPresent()) {
            UserSocialAccount account = existingAccount.get();
            account.setProviderId(userInfo.getId());
            account.setProviderEmail(email);
            account.setProviderUsername(userInfo.getName());
            account.setProviderAvatarUrl(userInfo.getImageUrl());
            account.setAccessToken(userRequest.getAccessToken().getTokenValue());
            if (userRequest.getAdditionalParameters().get("refresh_token") != null) {
                account.setRefreshToken(String.valueOf(userRequest.getAdditionalParameters().get("refresh_token")));
            }
            socialAccountRepository.save(account);
        } else {
            createSocialAccount(user, provider, userInfo, userRequest, email);
        }
    }

    private void createSocialAccount(User user, AuthProvider provider, OAuth2UserInfo userInfo,
                                     OAuth2UserRequest userRequest, String email) {
        UserSocialAccount socialAccount = UserSocialAccount.builder()
                .user(user)
                .provider(provider)
                .providerId(userInfo.getId())
                .providerEmail(email)
                .providerUsername(userInfo.getName())
                .providerAvatarUrl(userInfo.getImageUrl())
                .accessToken(userRequest.getAccessToken().getTokenValue())
                .refreshToken(userRequest.getAdditionalParameters().get("refresh_token") != null
                        ? String.valueOf(userRequest.getAdditionalParameters().get("refresh_token"))
                        : null)
                .build();
        socialAccountRepository.save(socialAccount);
    }

    private String generateUsername(String name, String email) {
        String base;
        if (StringUtils.hasText(name)) {
            base = name.toLowerCase().replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9_-]", "");
        } else {
            base = email.split("@")[0].replaceAll("[^a-zA-Z0-9_-]", "");
        }

        if (!StringUtils.hasText(base)) {
            base = "user";
        }

        String username = base;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + counter++;
        }
        return username;
    }
}