package com.treasuredigger.devel.service;

import com.treasuredigger.devel.dto.SessionMember;
import com.treasuredigger.devel.entity.Member;
import com.treasuredigger.devel.entity.OAuthAttributes;
import com.treasuredigger.devel.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    public CustomOAuth2UserService(MemberRepository memberRepository, HttpSession httpSession) {
        this.memberRepository = memberRepository;
        this.httpSession = httpSession;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionMember(member));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
        String provider = attributes.getRegistrationId(); // 제공자 이름 가져오기

        System.out.println("fffffffffffffffff"+attributes);

        Optional<Member> optionalMember = memberRepository.findByEmail(attributes.getEmail());

        Member member = optionalMember
                .map(entity -> {
                    entity.update(attributes.getName());
                    System.out.println("hhhhhhhhhhhhhhh"+entity);
                    return entity;
                })
                .orElseGet(() -> {
                    Member newMember = attributes.toEntity(); // 제공자를 사용
                    return newMember;
                });
        System.out.println("eeeeeeeeeeeeee"+member);


        return memberRepository.save(member);
    }
}

