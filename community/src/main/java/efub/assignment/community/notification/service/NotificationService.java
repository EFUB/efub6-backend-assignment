package efub.assignment.community.notification.service;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.service.MemberService;
import efub.assignment.community.notification.dto.response.NotificationListResponse;
import efub.assignment.community.notification.dto.summary.NotificationSummary;
import efub.assignment.community.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long memberId) {
        Member member = memberService.findByMemberId(memberId);

        List<NotificationSummary> notifications = notificationRepository
                .findAllByMemberOrderByCreatedAtDesc(member)
                .stream()
                .map(NotificationSummary::from)
                .toList();

        return new NotificationListResponse(notifications);
    }
}