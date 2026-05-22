package efub.assignment.community.notification.dto.response;

import efub.assignment.community.notification.dto.summary.NotificationSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationListResponse {

    private List<NotificationSummary> notification;
}