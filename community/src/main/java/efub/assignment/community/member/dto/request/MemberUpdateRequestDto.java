package efub.assignment.community.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@NoArgsConstructor //파라미터가 없는 디폴트 생성자를 생성
public class MemberUpdateRequestDto {
    private String nickname;
    private String email;

    @NotBlank //비밀번호는 required
    private String password;
}
