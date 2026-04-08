package efub.assignment.community.post.domain;

import efub.assignment.community.board.domain.Board;
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
public class Post extends BaseEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne (fetch = FetchType.LAZY)
    private Board board;

    @ManyToOne (fetch = FetchType.LAZY)
    private Member writer;

    @Column (nullable = false)
    private String title;

    @Column (nullable = false)
    private String content;

    @Column (nullable = false)
    private Long viewCount;

    @Builder
    public Post(Board board, Member writer, String title, String content) {
        this.board = board;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.viewCount = 0L;
    }

    public void changeContent(String newContent) {
        this.content = newContent;
    }
}
