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
    private Long boardId;

    @Column(nullable = false)
    private String boardName;

    @Column
    private String description;

    @Column
    private String notice;

    //게시판 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @Builder
    public Board(String boardName, String description, String notice, Member owner) {
        this.boardName = boardName;
        this.description = description;
        this.notice = notice;
        this.owner = owner;
    }

    public void changeOwner(Member newOwner){
        this.owner = newOwner;
    }

}
