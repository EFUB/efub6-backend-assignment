package efub.assignment.community.member.dto.request;

import lombok.Getter;

@Getter
public class ProfileUpdateRequestDto {
    private String email;
    private String password;
    private String nickname;
    private String university;
    private String studentId;

}
