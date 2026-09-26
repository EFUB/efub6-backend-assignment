package com.example.community.notification.service;

import com.example.community.member.domain.Member;
import com.example.community.member.service.MemberService;
import com.example.community.notification.domain.Notification;
import com.example.community.notification.domain.NotificationType;
import com.example.community.notification.dto.response.NotificationItem;
import com.example.community.notification.dto.response.NotificationListResponse;
import com.example.community.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberService memberService;

    // 댓글 알림 생성
    @Transactional
    public void createCommentNotification(Member receiver, String boardName, String commentContent) {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(NotificationType.COMMENT)
                .content("새로운 댓글이 달렸어요: " + commentContent)
                .boardName(boardName)
                .build();
        notificationRepository.save(notification);
    }

    // 쪽지방 알림 생성
    @Transactional
    public void createMessageRoomNotification(Member receiver) {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(NotificationType.MESSAGE_ROOM)
                .content("새로운 쪽지방이 생겼어요")
                .build();
        notificationRepository.save(notification);
    }

    // 알림 목록 조회
    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long memberId) {
        memberService.findByMemberId(memberId);

        List<NotificationItem> items = notificationRepository
                .findAllByReceiverMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(NotificationItem::from)
                .toList();

        return new NotificationListResponse(items, (long) items.size());
    }

    // 읽지 않은 알림 일괄 읽음 처리
    public void readAllNotifications(Long memberId) {
        memberService.findByMemberId(memberId);
        notificationRepository.markAllAsRead(memberId);
    }
}
