package efub.assignment.community.messageroom.domain;

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
public class Message extends BaseEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "messageroom_id", nullable = false)
    private MessageRoom messageRoom;

    @Column (name = "content")
    private String content;

    @Builder
    public Message (Member sender, MessageRoom messageRoom, String content) {
        this.messageRoom = messageRoom;
        this.sender = sender;
        this.content = content;
    }
}
