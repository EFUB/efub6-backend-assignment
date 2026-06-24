package efub.assignment.community.notification.domain;

public enum NotificationType {
    NEW_COMMENT_ON_POST("새로운 댓글이 달렸어요: "),
    NEW_MESSAGE_ROOM_CREATED("새로운 쪽지방이 생겼어요");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}