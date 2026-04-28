//package com.buildwithus.security.oauth2;
//
//import java.util.Map;
//
//public class GithubOAuth2UserInfo extends OAuth2UserInfo {
//
//    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
//        super(attributes);
//    }
//
//    @Override
//    public String getId() {
//        return String.valueOf(attributes.get("id"));
//    }
//
//    @Override
//    public String getName() {
//        return (String) attributes.get("name");
//    }
//
//    @Override
//    public String getEmail() {
//        return (String) attributes.get("email");
//    }
//
//    @Override
//    public String getImageUrl() {
//        return (String) attributes.get("avatar_url");
//    }
//}

package com.buildwithus.security.oauth2;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getName() {
        String name = (String) attributes.get("name");
        if (name == null || name.isBlank()) {
            name = (String) attributes.get("login");
        }
        return name;
    }

    @Override
    public String getEmail() {
        String email = (String) attributes.get("email");
        if (email == null || email.isBlank()) {
            Object login = attributes.get("login");
            if (login != null) {
                return login.toString() + "@users.noreply.github.com";
            }
        }
        return email;
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}