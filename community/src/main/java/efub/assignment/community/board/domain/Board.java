package efub.assignment.community.board.domain;

import efub.assignment.community.global.domain.BaseEntity;
import efub.assignment.community.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "boards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "board_name", nullable = false, length = 100)
    private String boardName;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String notice;

    @Builder
    public Board(Member member, String boardName, String description, String notice) {
        this.member = member;
        this.boardName = boardName;
        this.description = description;
        this.notice = notice;
    }

    public void updateBoardInfo(String boardName, String description, String notice) {
        this.boardName = boardName;
        this.description = description;
        this.notice = notice;
    }
}