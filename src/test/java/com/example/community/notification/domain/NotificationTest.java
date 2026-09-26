package com.example.community.notification.domain;

import com.example.community.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationTest {

    @Test
    @DisplayName("알림 내용이 30자를 초과하면 30자까지만 저장되고 말줄임표가 붙는다")
    void truncateContent_WhenExceeds30Characters() {
        // given
        Member receiver = Member.builder().build();
        String longContent = "새로운 알림이 생성되었습니다: " +
                "가나다라마바사아자차카타파아아야어여오요우유으이에이비씨디이에프지에이치아이제이케이";
        String truncatedContent = longContent.substring(0, 30) + "...";

        // when
        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(NotificationType.COMMENT)
                .content(longContent)
                .boardName("자유게시판")
                .build();

        // then
        assertThat(notification.getContent()).isEqualTo(truncatedContent);
    }

    @Test
    @DisplayName("알림 내용이 30자 미만이면 그대로 저장된다")
    void keepContent_WhenUnder30Characters() {
        // given
        Member receiver = Member.builder().build();
        String shortContent = "새로운 알림이 생성되었습니다: " + "안녕하세요!";

        // when
        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(NotificationType.COMMENT)
                .content(shortContent)
                .boardName("자유게시판")
                .build();

        // then
        assertThat(notification.getContent()).isEqualTo(shortContent);
    }
}
