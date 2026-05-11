package efub.assignment.community.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "예상치 못한 서버에러가 발생했습니다."),
    ERROR(400, "요청 처리에 실패했습니다."),

    // member
    MEMBER_NOT_FOUND(404, "존재하는 계정이 없습니다."),

    // post
    POST_NOT_FOUND(404, "해당 id의 게시물이 존재하지 않습니다."),
    POST_MEMBER_MISMATCH(401, "게시글 생성자가 아닙니다."),

    //board
    BOARD_NOT_FOUND(404, "존재하지 않는 게시판입니다. "),
    BOARD_ACCESS_DENIED(403, "권한이 없습니다.");

    private final int status;
    private final String message;
}
