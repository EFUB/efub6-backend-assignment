package efub.assignment.community.global.utils;

import java.util.Map;

public class OAuth2UserInfo {

    private Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    //카카오로그인 사용자 고유 id
    public Long getKakaoId() {
        return ((Number) attributes.get("id")).longValue();
    }

    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

    private Map<String, Object> getProfile() {
        return (Map<String, Object>) getKakaoAccount().get("profile");
    }

    public String getNickname() {
        return (String) getProfile().get("nickname");
    }

    public String getProfileImage() {
        return (String) getProfile().get("profile_image_url");
    }
}