package efub.assignment.community.member.service;

import efub.assignment.community.global.utils.KakaoUserInfo;
import efub.assignment.community.global.utils.OAuth2UserInfo;
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
        // OAuth2 사용자 정보 로드
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 카카오 OAuth2UserInfo 객체 생성
        OAuth2UserInfo oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());

        // DB에서 해당 사용자 조회 -> 없으면 새로 생성
        Member member = memberRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElseGet(() -> createMember(oAuth2UserInfo));

        // SuccessHandler에서 꺼내 쓸 수 있도록 attributes 재구성
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("email", member.getEmail());

        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),
                attributes,
                "email"
        );
    }

    private Member createMember(OAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .nickname(oAuth2UserInfo.getNickname())
                .password("")
                .build();
        return memberRepository.save(member);
    }
}