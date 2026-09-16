package efub.assignment.community.global.utils;

import java.util.Map;

public class KakaoOAuth2UserInfo {

    private Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getNickname() {
        Map<String, Object> profile = getMap(getKakaoAccount(), "profile");
        return profile == null ? null : (String) profile.get("nickname");
    }

    public String getEmail() {
        Map<String, Object> kakaoAccount = getKakaoAccount();
        return kakaoAccount == null ? null : (String) kakaoAccount.get("email");
    }

    private Map<String, Object> getKakaoAccount() {
        return getMap(attributes, "kakao_account");
    }
    private Map<String, Object> getMap(Map<String, Object> source, String key) {
        if (source == null) return null;
        return (Map<String, Object>) source.get(key);
    }







}
