package efub.assignment.community.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    // 1. 구체적인 네이밍으로 변경 + 알림 문구를 괄호 안에 저장!
    NEW_COMMENT_ON_POST("새로운 댓글이 달렸어요: "),
    NEW_MESSAGE_ROOM_CREATED("새로운 쪽지방이 생겼어요");

    // 2. Enum이 가질 문자열 필드
    private final String messagePrefix;
}