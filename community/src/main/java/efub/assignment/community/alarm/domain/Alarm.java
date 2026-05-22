package efub.assignment.community.alarm.domain;

import efub.assignment.community.alarm.enums.AlarmType;
import efub.assignment.community.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmType alarmType;

    @Column
    private String boardName;

    @Column(nullable = false)
    private String content;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Alarm(Member receiver, AlarmType alarmType, String boardName, String content) {
        this.receiver = receiver;
        this.alarmType = alarmType;
        this.boardName = boardName;
        this.content = content;
    }
}
