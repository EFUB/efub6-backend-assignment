package efub.assignment.community.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR (500, "예상치 못한 서버 에러가 발생했습니다."),
    ERROR(400, "요청 처리에 실패했습니다."),

    // member
    MEMBER_NOT_FOUND(404, "존재하는 계정이 없습니다."),

    // post
    POST_NOT_FOUND(404, "해당 id의 게시물이 존재하지 않습니다."),
    POST_ACCOUNT_MISMATCH(401, "게시글 생성자가 아닙니다."),

    //board
    BOARD_NOT_FOUND(404, "해당 id의 게시판이 존재하지 않습니다."),
    BOARD_ACCOUNT_MISTMATCH(401, "게시판 주인이 아닙니다."),
    COMMENT_NOT_FOUND(404, "해당 id의 댓글이 존재하지 않습니다."),
    COMMENT_ACCOUNT_MISMATCH(401, "댓글의 주인이 아닙니다"),
    POST_LIKE_ALREADY_EXISTS(400, "게시글에 이미 좋아요를 눌렀습니다"),
    POST_LIKE_NOT_FOUND(404, "게시글에 좋아요가 존재하지 않습니다"),
    COMMENT_LIKE_ALREADY_EXISTS(400, "좋아요가 이미 존재합니다."),
    COMMENT_LIKE_NOT_FOUND(404, "좋아요가 존재하지 않습니다."),
    MESSAGEROOM_ALREADY_EXISTS(400, "쪽지방이 이미 존재합니다."),
    MESSAGEROOM_NOT_FOUND(404, "쪽지방이 존재하지 않습니다."),
    MESSAGEROOM_MEMBER_MISMATCH(401, "쪽지방의 주인이 아닙니다.");

    private final int status;
    private final String message;
}
