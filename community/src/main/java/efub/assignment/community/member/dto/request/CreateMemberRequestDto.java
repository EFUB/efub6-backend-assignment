package efub.assignment.community.member.dto.request;

import efub.assignment.community.member.domain.Member;
import lombok.Getter;

@Getter
public class CreateMemberRequestDto {
    private String password;
    private String email;
    private String nickname;
    private String university;
    private String studentId;


    public Member toEntity() {
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .university(this.university)
                .studentId(this.studentId)
                .build();
    }
}
