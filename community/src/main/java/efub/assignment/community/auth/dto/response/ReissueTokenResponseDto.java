package efub.assignment.community.auth.dto.response;

import lombok.Builder;

public record ReissueTokenResponseDto(String accessToken) {
    @Builder
    public ReissueTokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

}