package com.example.community.notification.domain;

import com.example.community.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationTest {

    @ParameterizedTest
    @DisplayName("알림 내용의 길이에 따라 30자 기준으로 처리한다")
    @CsvSource(value = {
            // 1. 30자 미만
            "새로운 알림이 생성되었습니다: 안녕하세요! | 새로운 알림이 생성되었습니다: 안녕하세요!",
            // 2. (경계값) 30자
            "새로운 알림이 생성되었습니다: 안녕하세요! 알림입니다. | 새로운 알림이 생성되었습니다: 안녕하세요! 알림입니다.",
            // 3. 30자 초과
            "새로운 알림이 생성되었습니다: 가나다라마바사아자차카타파아아야어여오요우유으이에이비씨디이에프지에이치아이제이케이" +
                    "| 새로운 알림이 생성되었습니다: 가나다라마바사아자차카타파..."
    }, delimiter = '|')
    void truncateContent_BasedOnLength(String inputContent, String expectedContent) {
        // given
        Member receiver = Member.builder().build();

        // when
        Notification notification = Notification.builder()
                .receiver(receiver)
                .type(NotificationType.COMMENT)
                .content(inputContent)
                .boardName("자유게시판")
                .build();

        // then
        assertThat(notification.getContent()).isEqualTo(expectedContent);
    }

}
