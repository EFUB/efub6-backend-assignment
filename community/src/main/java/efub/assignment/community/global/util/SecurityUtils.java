package efub.assignment.community.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
public class SecurityUtils {

    public static String getCurrentUserNickname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return authentication.getName();
    }
}
