package efub.assignment.community.notification.service;

import efub.assignment.community.global.exception.CustomException;
import efub.assignment.community.global.exception.ErrorCode;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.notification.domain.Notification;
import efub.assignment.community.notification.domain.NotificationType;
import efub.assignment.community.notification.dto.response.NotificationListResponseDto;
import efub.assignment.community.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public NotificationListResponseDto getNotifications(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return NotificationListResponseDto.from(
                notificationRepository.findAllByMemberOrderByCreatedAtDesc(member)
        );
    }

    @Transactional
    public void createCommentNotification(Member receiver, String boardName, String commentContent) {
        Notification notification = Notification.builder()
                .member(receiver)
                .type(NotificationType.COMMENT)
                .boardName(boardName)
                .content("새로운 댓글이 달렸어요: " + commentContent)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void createMessageRoomNotification(Member receiver) {
        Notification notification = Notification.builder()
                .member(receiver)
                .type(NotificationType.MESSAGE_ROOM)
                .boardName(null)
                .content("새로운 쪽지방이 생겼어요")
                .build();

        notificationRepository.save(notification);
    }
}