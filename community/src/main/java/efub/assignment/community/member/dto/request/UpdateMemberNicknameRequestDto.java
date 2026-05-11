package efub.assignment.community.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


public record UpdateMemberNicknameRequestDto(@NotBlank String nickname) {
}
