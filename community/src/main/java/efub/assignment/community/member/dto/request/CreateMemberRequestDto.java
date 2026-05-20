package efub.assignment.community.member.dto.request;

import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record CreateMemberRequestDto(
        @NotBlank(message = "학번은 필수입니다.")
        String studentId,

        @NotBlank(message = "대학교명은 필수입니다.")
        String university,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {

    public Member toEntity() {
        return Member.builder()
                .studentId(studentId)
                .university(university)
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();
    }
}