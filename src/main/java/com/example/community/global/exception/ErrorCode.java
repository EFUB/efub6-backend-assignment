package com.example.community.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Default
    INTERNAL_SERVER_ERROR(500, "예상치 못한 서버에러가 발생했습니다."),
    ERROR(400, "요청 처리에 실패했습니다."),

    // member
    MEMBER_NOT_FOUND(404, "존재하는 회원이 없습니다."),
    MEMBER_EMAIL_ALREADY_EXISTS(409, "이미 가입된 이메일입니다."),

    // board
    BOARD_NOT_FOUND(404, "해당 id의 게시판이 존재하지 않습니다."),
    BOARD_ACCOUNT_MISMATCH(403, "게시판 주인이 아닙니다."),

    // post
    POST_NOT_FOUND(404, "해당 id의 게시물이 존재하지 않습니다."),
    POST_ACCOUNT_MISMATCH(403, "게시글 생성자가 아닙니다."),
    POST_LIKE_ALREADY_EXISTS(409, "이미 좋아요를 누른 게시글입니다."),
    POST_LIKE_NOT_FOUND(404, "좋아요가 존재하지 않습니다."),

    // comment
    COMMENT_NOT_FOUND(404, "해당 id의 댓글이 존재하지 않습니다."),
    COMMENT_ACCOUNT_MISMATCH(403, "댓글 작성자가 아닙니다."),

    // message-room
    MESSAGE_ROOM_NOT_FOUND(404, "해당 쪽지방을 찾을 수 없습니다."),
    MESSAGE_ROOM_ALREADY_EXISTS(409, "이미 존재하는 쪽지방입니다."),
    MESSAGE_ROOM_SELF_SEND(400, "자기 자신에게 쪽지를 보낼 수 없습니다."),
    MESSAGE_ROOM_ACCOUNT_MISMATCH(403, "쪽지방 참여자가 아닙니다.");

    private final int status;
    private final String message;
}
