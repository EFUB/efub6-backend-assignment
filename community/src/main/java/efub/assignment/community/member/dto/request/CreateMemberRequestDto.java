package efub.assignment.community.member.dto.request;

import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMemberRequestDto {
    @NotBlank
    private String studentId;
    @NotBlank
    private String university;
    @NotBlank
    private String nickname;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    public Member toEntity(){
        return Member.builder()
                .studentId(studentId)
                .university(university)
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();
    }
}
