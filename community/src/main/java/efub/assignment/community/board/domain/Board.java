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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    @Column(nullable = false)
    private String boardname;

    @Column
    private String description;

    @Column
    private String notice;

    @Builder
    public Board(Member owner, String boardname, String description, String notice) {
        this.owner = owner;
        this.boardname = boardname;
        this.description = description;
        this.notice = notice;
    }

    // 게시판 주인 변경
    public void changeOwner(Member newOwner) {this.owner = newOwner;}
}
