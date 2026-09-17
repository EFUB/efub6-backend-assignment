package efub.assignment.community.global.util;

import java.util.Map;

public class OAuth2UserInfo {

    private Map<String, Object> attributes;

    public  OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    // 닉네임 반환
    public String getNickname() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return profile == null ? null : (String) profile.get("nickname");
    }
}
