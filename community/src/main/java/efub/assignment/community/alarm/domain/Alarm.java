package efub.assignment.community.alarm.domain;

import efub.assignment.community.alarm.enums.AlarmType;
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
public class Alarm extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmType type;

    @Column(nullable = false)
    private String content;

    private String boardTitle; // ex) "새내기 게시판"

    @Builder
    public Alarm(Member receiver, AlarmType type, String content, String boardTitle) {
        this.receiver = receiver;
        this.type = type;
        this.content = content;
        this.boardTitle = boardTitle;
    }
}