package efub.assignment.community.member.dto.request;

import efub.assignment.community.member.domain.Member;
import lombok.Getter;

// 회원 가입 Request DTO
@Getter
public class CreateMemberRequestDto {

    private String email;

    private String password;

    private String nickname;

    private String university;

    private String studentId;

    // Member 객체로 build
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .university(university)
                .studentId(studentId)
                .build();
    }
}
