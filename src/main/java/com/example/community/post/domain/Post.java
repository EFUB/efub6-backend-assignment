package com.example.community.post.domain;

import com.example.community.board.domain.Board;
import com.example.community.global.domain.BaseEntity;
import com.example.community.global.exception.CustomException;
import com.example.community.global.exception.ErrorCode;
import com.example.community.member.domain.Member;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    @Column(nullable = false)
    private Boolean isAnonymous;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Builder
    public Post(Board board, Member writer, Boolean isAnonymous, String title, String content) {
        this.board = board;
        this.writer = writer;
        this.isAnonymous = isAnonymous;
        this.title = title;
        this.content = content;
    }

    // TDD 과제 주석 처리
    public void changePost(String title, String content) {
        if (title != null) {
            if (title.isBlank()) {
                throw new CustomException(ErrorCode.POST_INVALID_TITLE);
            }
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }

}
