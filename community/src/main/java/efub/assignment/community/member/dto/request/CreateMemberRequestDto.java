package efub.assignment.community.member.dto.request;

import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateMemberRequestDto {
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    private String nickname;
    @NotBlank
    private String university;
    @NotBlank
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
