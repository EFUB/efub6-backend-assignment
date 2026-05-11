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

    // 게시판 주인 닉네임
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    // 게시판 이름
    @Column(nullable = false)
    private String name;

    // 게시판 설명
    @Column(nullable = true)
    private String description;

    // 게시판 공지
    @Column(nullable = true)
    private String notification;

    @Builder
    public Board(Member writer, String name, String description, String notification) {
        this.writer = writer;
        this.name = name;
        this.description = description;
        this.notification = notification;
    }

    // 게시판 주인 변경 메서드
    public void changeOwner(Member newOwner) {
        this.writer = newOwner;
    }
}
