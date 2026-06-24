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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member boardOwner;

    @Column (nullable = false)
    private String title;

    @Column
    private String description;

    @Column
    private String notice;

    @Builder
    public Board(Member boardOwner, String title, String description, String notice) {
        this.boardOwner = boardOwner;
        this.title = title;
        this.description = description;
        this.notice = notice;
    }

    public void changeBoardOwner(Member boardOwner) {
        this.boardOwner = boardOwner;
    }
}
