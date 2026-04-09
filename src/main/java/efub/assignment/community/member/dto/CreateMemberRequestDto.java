package efub.assignment.community.member.dto;

import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 계정 생성 Request DTO
@Getter
@NoArgsConstructor
public class CreateMemberRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private String school;

    @NotBlank
    private String studentId;


    // Member 객체로 build
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .school(school)
                .studentId(studentId)
                .build();
    }
}
