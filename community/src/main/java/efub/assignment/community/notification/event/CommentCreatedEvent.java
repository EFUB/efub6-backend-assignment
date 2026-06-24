package efub.assignment.community.notification.event;

public record CommentCreatedEvent(
        Long receiverId,
        String boardName,
        String commentContent
) {
}