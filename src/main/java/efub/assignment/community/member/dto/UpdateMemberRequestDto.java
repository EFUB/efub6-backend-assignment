package efub.assignment.community.member.dto;

import efub.assignment.community.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMemberRequestDto {

    @NotBlank
    private String nickname;


    public Member toEntity() {
        return Member.builder()
                .nickname(nickname)
                .build();
    }
}
