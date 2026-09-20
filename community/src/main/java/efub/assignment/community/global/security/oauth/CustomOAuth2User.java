package efub.assignment.community.global.security.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CustomOAuth2User implements OAuth2User {

    private final Long memberId;
    private final String providerId;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(
            Long memberId,
            String providerId,
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes
    ) {
        this.memberId = Objects.requireNonNull(memberId, "멤버 ID는 필수입니다.");
        this.providerId = Objects.requireNonNull(providerId, "OAuth 제공자 회원 식별자는 필수입니다.");
        this.authorities = List.copyOf(authorities);
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getProviderId() {
        return providerId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return providerId;
    }
}
