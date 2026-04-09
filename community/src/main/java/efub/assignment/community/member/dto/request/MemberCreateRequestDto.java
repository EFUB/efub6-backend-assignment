package efub.assignment.community.member.dto.request;

import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

//멤버 생성
@Getter
public class MemberCreateRequestDto {
    @NotNull
    private Long studentId;

    @NotNull
    private String university;

    @NotNull
    private String nickname;

    @NotNull
    private String email;

    @NotNull
    private String password;

    //Member 객체로 build
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
