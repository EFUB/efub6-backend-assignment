package efub.assignment.community.notification.domain;

import efub.assignment.community.global.domain.BaseEntity;
import efub.assignment.community.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String content;

    private String boardName;

    @Builder
    public Notification(Member receiver, NotificationType type, String content, String boardName) {
        this.receiver = receiver;
        this.type = type;
        this.content = content;
        this.boardName = boardName;
    }
}
