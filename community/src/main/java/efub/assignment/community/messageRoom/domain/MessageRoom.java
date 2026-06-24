package efub.assignment.community.messageRoom.domain;

import efub.assignment.community.global.domain.BaseEntity;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.message.domain.Message;
import efub.assignment.community.post.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "message_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_room_id")
    private Long messageRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id",nullable = false)
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id",nullable = false)
    private Member participant;

    @Builder
    public MessageRoom(Post post,Member creator,Member participant){
        this.post = post;
        this.creator = creator;
        this.participant = participant;
    }

    public boolean isParticipant(Long memberId) {
        return creator.getMemberId().equals(memberId)
                || participant.getMemberId().equals(memberId);
    }

    public Long getPartnerId(Long memberId) {
        if (creator.getMemberId().equals(memberId)) {
            return participant.getMemberId();
        }

        return creator.getMemberId();
    }

}
