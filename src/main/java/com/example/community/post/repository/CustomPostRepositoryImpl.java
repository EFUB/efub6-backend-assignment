package com.example.community.post.repository;

import com.example.community.board.domain.QBoard;
import com.example.community.member.domain.QMember;
import com.example.community.post.domain.Post;
import com.example.community.post.domain.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> search(String keyword) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        QBoard board = QBoard.board;

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(board.name.containsIgnoreCase(keyword)
                    .or(post.title.containsIgnoreCase(keyword))
                    .or(post.content.containsIgnoreCase(keyword))
                    // 익명 게시글 제외
                    .or(post.isAnonymous.isFalse().and(member.nickname.containsIgnoreCase(keyword)))
            );
        }

        return queryFactory.selectFrom(post)
                .join(post.writer, member).fetchJoin()
                .join(post.board, board).fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}
