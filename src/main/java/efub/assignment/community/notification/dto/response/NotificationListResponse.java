package efub.assignment.community.notification.dto.response;

import java.util.List;

public record NotificationListResponse(
        List<NotificationResponse> notifications
) {
}