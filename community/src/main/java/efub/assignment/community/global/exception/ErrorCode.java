package efub.assignment.community.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Default
    INTERNAL_SERVER_ERROR(500, "예상치 못한 서버 에러가 발생했습니다."),
    INVALID_INPUT(400, "잘못된 요청입니다."),

    // MEMBER
    MEMBER_NOT_FOUND(404, "해당 id의 멤버가 존재하지 않습니다."),
    DUPLICATE_EMAIL(400, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(400, "이미 사용 중인 닉네임입니다."),

    // BOARD
    BOARD_NOT_FOUND(404, "해당 id의 게시판이 존재하지 않습니다."),
    BOARD_MEMBER_MISMATCH(403, "게시판 작성자가 아닙니다."),

    // POST
    POST_NOT_FOUND(404, "해당 id의 게시글이 존재하지 않습니다."),
    POST_MEMBER_MISMATCH(403, "게시글 작성자가 아닙니다."),

    // Comment
    COMMENT_NOT_FOUND(404, "해당 id의 댓글이 존재하지 않습니다.");

    private final int status;
    private final String message;
}