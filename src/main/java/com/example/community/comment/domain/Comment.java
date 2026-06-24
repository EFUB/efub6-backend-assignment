package com.example.community.comment.domain;

import com.example.community.global.domain.BaseEntity;
import com.example.community.member.domain.Member;
import com.example.community.post.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private Boolean isAnonymous;

    @Builder
    public Comment(Post post, Member writer, String content, Boolean isAnonymous) {
        this.post = post;
        this.writer = writer;
        this.content = content;
        this.isAnonymous = isAnonymous;
    }

    public void changeContent(String content) {
        if (content != null) {
            this.content = content;
        }
    }
}
