package efub.assignment.community.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthMemberResponseDto {

    private Long memberId;
    private String nickname;
    private String profileImage;
}