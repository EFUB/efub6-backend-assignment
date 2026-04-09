package efub.assignment.community.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

//에러 메세지 응답 DTO
@Getter
@AllArgsConstructor // ppt는 @Builder인데 요걸 쓰는게 더 좋을까요?(실습 기준)
public class ErrorResponseDto {
    private String message;
    private int status;
}
