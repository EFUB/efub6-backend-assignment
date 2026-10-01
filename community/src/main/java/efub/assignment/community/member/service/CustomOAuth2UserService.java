package efub.assignment.community.member.service;

import efub.assignment.community.global.util.OAuth2UserInfo;
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

    // OAuth2UserRequest를 받아 사용자 로드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = new OAuth2UserInfo(oAuth2User.getAttributes());

//        Member member = memberRepository.findByEmail(oAuth2UserInfo.getEmail())
//                .orElseGet(() -> createMember(oAuth2UserInfo));

        Member member = memberRepository.findByNickname(oAuth2UserInfo.getNickname())
                .orElseGet(() -> createMember(oAuth2UserInfo));

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("id", member.getMemberId());
        attributes.put("nickname", member.getNickname());

        // DefaultOAuth2User 객체 생성하여 반환
        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),
                attributes,
                "nickname"); // 기본 식별자 지정
    }

    private Member createMember(OAuth2UserInfo oAuth2UserInfo) {
        Member member = Member.builder()
                .password("")
                .nickname(oAuth2UserInfo.getNickname())
                .build();
        return memberRepository.save(member);
    }
}
