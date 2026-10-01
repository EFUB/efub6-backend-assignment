package com.example.community.notification.domain;

import com.example.community.global.domain.BaseEntity;
import com.example.community.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationType type;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(length = 50)
    private String boardName;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Builder
    public Notification(Member receiver, NotificationType type, String content, String boardName) {
        this.receiver = receiver;
        this.type = type;
        this.content = truncate(content);
        this.boardName = boardName;
    }

    public void read() {
        this.isRead = true;
    }

    private String truncate(String text) {
        if (text == null || text.length() <= 30) {
            return text;
        }
        return text.substring(0, 30) + "...";
    }

}
