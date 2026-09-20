package efub.assignment.community.global.security;

import java.security.Principal;
import java.util.Objects;

public record AuthenticatedMember(Long memberId) implements Principal {

    public AuthenticatedMember {
        Objects.requireNonNull(memberId, "멤버 ID는 필수입니다.");
    }

    @Override
    public String getName() {
        return memberId.toString();
    }
}
