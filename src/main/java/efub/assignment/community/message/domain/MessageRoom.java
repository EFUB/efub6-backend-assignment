package efub.assignment.community.message.domain;

import efub.assignment.community.global.domain.BaseEntity;
import efub.assignment.community.member.domain.Member;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Member partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 첫 메시지도 여기로 넣기
    @OneToMany(mappedBy = "messageRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @Builder
    public MessageRoom(Member creator, Member partner, Post post) {
        this.creator = creator;
        this.partner = partner;
        this.post = post;
    }

    public Member getPartner(Long memberId) {
        if (this.creator.getMemberId().equals(memberId)) {
            return this.partner;
        }
        return this.creator;
    }

    public boolean isParticipant(Member member) {
        // ID 값으로 안전하게 비교하거나 객체로 비교!
        Long memberId = member.getMemberId();
        return this.creator.getMemberId().equals(memberId) || this.partner.getMemberId().equals(memberId);
    }
}
