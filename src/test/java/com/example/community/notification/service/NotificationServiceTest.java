package com.example.community.notification.service;

import com.example.community.member.domain.Member;
import com.example.community.member.service.MemberService;
import com.example.community.notification.domain.Notification;
import com.example.community.notification.domain.NotificationType;
import com.example.community.notification.dto.response.NotificationListResponse;
import com.example.community.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private NotificationService notificationService;

    private Long memberId;
    private Member member;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        member = Member.builder().build();

        when(memberService.findByMemberId(memberId)).thenReturn(member);
    }

    private Notification notificationFixture(
            NotificationType type,
            String content,
            String boardName
    ) {
        return Notification.builder()
                .receiver(member)
                .type(type)
                .content(content)
                .boardName(boardName)
                .build();
    }

    @Test
    @DisplayName("회원의 알림 목록을 조회한다")
    void getNotifications_ReturnsNotificationsDescendingOrder() {
        // given
        Notification firstNotification = notificationFixture(
                NotificationType.COMMENT, "첫 번째 알림", "자유게시판"
        );
        Notification secondNotification = notificationFixture(
                NotificationType.MESSAGE_ROOM, "두 번째 알림", "자유게시판"
        );
        when(notificationRepository.findAllByReceiverMemberIdOrderByCreatedAtDesc(memberId))
                .thenReturn(List.of(firstNotification, secondNotification));

        // when
        NotificationListResponse response = notificationService.getNotifications(memberId);

        // then
        assertThat(response.notifications()).hasSize(2);
        assertThat(response.totalNotifications()).isEqualTo(2);
    }

    @Test
    @DisplayName("알림이 없으면 빈 알림 목록을 반환한다")
    void getNotifications_ReturnsEmptyList_WhenNoNotifications() {
        // given
        when(notificationRepository.findAllByReceiverMemberIdOrderByCreatedAtDesc(memberId))
                .thenReturn(List.of());

        // when
        NotificationListResponse response = notificationService.getNotifications(memberId);

        // then
        assertThat(response.notifications()).isEmpty();
        assertThat(response.totalNotifications()).isZero();
    }

}
