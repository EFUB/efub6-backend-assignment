package efub.assignment.community.messageroom.domain;

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
    @Column(name = "messageroom_id")
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Member creator;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Member target;

    @OneToMany (mappedBy = "messageRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Message> messages = new ArrayList<>();

    @Builder
    public MessageRoom (Post post, Member creator, Member target) {
        this.post = post;
        this.creator = creator;
        this.target = target;
    }

    public void addMessage (Message message) {
        this.messages.add(message);
    }

    public Message getFirstMessage () {
        return messages.isEmpty() ? null : messages.get(0);
    }
}
