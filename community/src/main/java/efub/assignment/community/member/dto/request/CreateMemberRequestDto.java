package efub.assignment.community.member.dto.request;

import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record CreateMemberRequestDto(
        String studentId,
        String university,
        String nickname,
        String email,
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