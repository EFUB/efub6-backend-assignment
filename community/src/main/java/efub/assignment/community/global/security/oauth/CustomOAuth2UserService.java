package efub.assignment.community.global.security.oauth;

import efub.assignment.community.member.domain.AuthProvider;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.MemberStatus;
import efub.assignment.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String KAKAO_REGISTRATION_ID = "kakao";
    private static final String USER_ROLE = "ROLE_USER";

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        return processOAuth2User(registrationId, oauth2User);
    }

    CustomOAuth2User processOAuth2User(String registrationId, OAuth2User oauth2User) {
        if (!KAKAO_REGISTRATION_ID.equals(registrationId)) {
            throw oauth2Exception("unsupported_provider", "지원하지 않는 OAuth 제공자입니다: " + registrationId);
        }

        Map<String, Object> attributes = oauth2User.getAttributes();
        String providerId = extractProviderId(attributes);
        Map<String, Object> kakaoAccount = nestedMap(attributes, "kakao_account");
        Map<String, Object> profile = nestedMap(kakaoAccount, "profile");
        String email = nullableString(kakaoAccount.get("email"));
        String nickname = nullableString(profile.get("nickname"));
        String profileImage = nullableString(profile.get("profile_image_url"));

        Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(
                AuthProvider.KAKAO,
                providerId
        );

        Member member;
        if (existingMember.isPresent()) {
            member = existingMember.get();
            if (member.getStatus() == MemberStatus.UNREGISTER) {
                throw oauth2Exception("unregistered_member", "탈퇴한 회원입니다.");
            }
            member.updateOAuthProfile(email, nickname, profileImage);
        } else {
            member = memberRepository.save(
                    Member.createKakao(providerId, email, nickname, profileImage)
            );
        }

        Set<GrantedAuthority> authorities = new LinkedHashSet<>(oauth2User.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(USER_ROLE));

        return new CustomOAuth2User(
                member.getMemberId(),
                providerId,
                authorities,
                attributes
        );
    }

    private String extractProviderId(Map<String, Object> attributes) {
        Object id = attributes.get("id");
        if (!(id instanceof Number) && !(id instanceof String)) {
            throw oauth2Exception("invalid_user_info", "카카오 회원 식별자를 찾을 수 없습니다.");
        }

        String providerId = id.toString();
        if (providerId.isBlank()) {
            throw oauth2Exception("invalid_user_info", "카카오 회원 식별자를 찾을 수 없습니다.");
        }
        return providerId;
    }

    private Map<String, Object> nestedMap(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (!(value instanceof Map<?, ?> rawMap)) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        rawMap.forEach((rawKey, rawValue) -> {
            if (rawKey instanceof String stringKey) {
                result.put(stringKey, rawValue);
            }
        });
        return result;
    }

    private String nullableString(Object value) {
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            return null;
        }
        return stringValue;
    }

    private OAuth2AuthenticationException oauth2Exception(String errorCode, String message) {
        return new OAuth2AuthenticationException(new OAuth2Error(errorCode), message);
    }
}
