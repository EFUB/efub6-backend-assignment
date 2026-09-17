package efub.assignment.community.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponseDto {
    private String accessToken;

    @Builder
    public TokenResponseDto(final String accessToken) { this.accessToken = accessToken; }
}
