package efub.assignment.community.global.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 현재 인증된 사용자 memberId 반환
     * 인증 정보가 없는 경우 null 반환
     */
    public static Long getCurrentMemberId() {

        // SecurityContext에서 현재 인증 정보 가져오기
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 사용자 식별자가 없는 경우
        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        return Long.valueOf(authentication.getName());
    }
}