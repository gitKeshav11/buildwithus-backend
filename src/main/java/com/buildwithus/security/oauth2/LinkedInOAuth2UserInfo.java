//package com.buildwithus.security.oauth2;
//
//import java.util.Map;
//
//public class LinkedInOAuth2UserInfo extends OAuth2UserInfo {
//
//    public LinkedInOAuth2UserInfo(Map<String, Object> attributes) {
//        super(attributes);
//    }
//
//    @Override
//    public String getId() {
//        return (String) attributes.get("id");
//    }
//
//    @Override
//    public String getName() {
//        String firstName = (String) attributes.get("localizedFirstName");
//        String lastName = (String) attributes.get("localizedLastName");
//        return firstName + " " + lastName;
//    }
//
//    @Override
//    public String getEmail() {
//        return (String) attributes.get("emailAddress");
//    }
//
//    @Override
//    public String getImageUrl() {
//        return (String) attributes.get("pictureUrl");
//    }
//}
package com.buildwithus.security.oauth2;

import java.util.Map;

public class LinkedInOAuth2UserInfo extends OAuth2UserInfo {

    public LinkedInOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getName() {
        String firstName = (String) attributes.get("localizedFirstName");
        String lastName = (String) attributes.get("localizedLastName");

        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";

        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? "LinkedIn User" : fullName;
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        if (email != null) {
            return String.valueOf(email);
        }
        return null;
    }

    @Override
    public String getImageUrl() {
        Object picture = attributes.get("picture");
        if (picture != null) {
            return String.valueOf(picture);
        }
        return null;
    }
}
