package efub.assignment.community.auth.service;

import efub.assignment.community.global.utils.KakaoOAuth2UserInfo;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        KakaoOAuth2UserInfo kakaoOAuth2UserInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());

        Member member = memberRepository.findByEmail(kakaoOAuth2UserInfo.getEmail())
                .orElseGet(() -> createMember(kakaoOAuth2UserInfo));

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("id", member.getMemberId());
        attributes.put("email", member.getEmail());

        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),
                attributes,
                "email");
    }

    private Member createMember(KakaoOAuth2UserInfo kakaoOAuth2UserInfo) {
        Member member = Member.builder()
                .nickname(kakaoOAuth2UserInfo.getNickname())
                .password("")
                .email(kakaoOAuth2UserInfo.getEmail())
                .university("")
                .build();

        return memberRepository.save(member);
    }
}
