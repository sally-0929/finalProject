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
    private String id;

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
                .id((String) attributes.get("sub"))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .id((String) response.get("id"))
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(response)
                .nameAttributeKey("id") // Naver에서 사용할 사용자 ID 속성
                .registrationId("naver") // 제공자 정보 설정
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) attributes.get("properties");

        // null 체크
        if (response == null || profile == null) {
            throw new IllegalArgumentException("Invalid Kakao response: Missing account or profile");
        }

        // Kakao ID를 가져올 때 Long을 String으로 변환
        Object kakaoIdObj = attributes.get("id");
        String kakaoId = (kakaoIdObj instanceof Long) ? String.valueOf(kakaoIdObj) : (String) kakaoIdObj;

        return OAuthAttributes.builder()
                .id(kakaoId)
                .name((String) profile.get("nickname"))
                .email((String) response.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Member toEntity() {
        // Member 객체를 생성
        Member member = Member.builder()
                .mid(id)
                .name(name)
                .email(email)
                .role(Role.MEMBER)
                .provider(registrationId)
                .build();

        // MemberGrade 객체 생성
        MemberGrade memberGrade = new MemberGrade(member); // MemberGrade 생성 시 Member 객체 전달

        // Member 객체에 MemberGrade 설정
        member.setMemberGrade(memberGrade);

        return member;
    }
}
