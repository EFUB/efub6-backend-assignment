package efub.assignment.community.notification.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.notification.domain.Notification;
import efub.assignment.community.notification.domain.NotificationType;
import efub.assignment.community.notification.dto.response.NotificationListResponse;
import efub.assignment.community.notification.dto.response.NotificationResponse;
import efub.assignment.community.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void createCommentNotification(Member receiver, String boardName, String commentContent) {
        String notificationContent = NotificationType.NEW_COMMENT_ON_POST.getMessagePrefix() + commentContent;

        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(NotificationType.NEW_COMMENT_ON_POST)
                .content(notificationContent)
                .boardName(boardName)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public NotificationListResponse getAllNotifications(Long memberId) {
        List<NotificationResponse> notificationResponses = notificationRepository.findAllByReceiverMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(NotificationResponse::from)
                .toList();

        return new NotificationListResponse(notificationResponses);
    }
}
