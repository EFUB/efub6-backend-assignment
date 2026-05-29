package efub.assignment.community.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//에러 메세지 응답 DTO
@Getter
@Builder
public class ErrorResponseDto {
    private String message;
    private int status;
}
