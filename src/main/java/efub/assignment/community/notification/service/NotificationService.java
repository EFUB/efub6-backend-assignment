package efub.assignment.community.notification.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.notification.domain.Notification;
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
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getAllNotifications(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        List<NotificationResponse> notificationResponses = notificationRepository.findAllByReceiverOrderByCreatedAtDesc(member)
                .stream()
                .map(NotificationResponse::from)
                .toList();

        return new NotificationListResponse(notificationResponses);
    }
}
