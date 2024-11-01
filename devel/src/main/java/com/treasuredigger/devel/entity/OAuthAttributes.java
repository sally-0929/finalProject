package com.treasuredigger.devel.entity;

import com.treasuredigger.devel.constant.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String registrationId; // 제공자 정보를 추가

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        OAuthAttributes oAuthAttributes;

        if ("naver".equals(registrationId)) {
            oAuthAttributes = ofNaver(userNameAttributeName, attributes);
        } else if ("kakao".equals(registrationId)) {
            oAuthAttributes = ofKakao(userNameAttributeName, attributes);
        } else {
            oAuthAttributes = ofGoogle(userNameAttributeName, attributes);
        }

        oAuthAttributes.setRegistrationId(registrationId); // 제공자 정보 설정
        return oAuthAttributes;
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) attributes.get("properties");

        // null 체크
        if (response == null || profile == null) {
            throw new IllegalArgumentException("Invalid Kakao response: Missing account or profile");
        }

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) response.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .role(Role.USER)
                .provider(registrationId) // 제공자 설정
                .build();
    }
}
