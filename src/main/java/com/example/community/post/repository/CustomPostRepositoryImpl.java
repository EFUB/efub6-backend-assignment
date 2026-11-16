package com.example.community.post.repository;

import com.example.community.post.domain.Post;
import com.example.community.post.dto.condition.PostSearchCondition;
import com.example.community.post.enums.PostSort;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.community.post.domain.QPost.post;
import static com.example.community.post.domain.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> search(PostSearchCondition cond) {
        return jpaQueryFactory
                .selectFrom(post)
                .leftJoin(postLike).on(postLike.post.eq(post))
                .where(keywordContains(cond.keyword()),
                        writerNicknameEq(cond.authorNickname()))
                .groupBy(post.id)
                .orderBy(orderBy(cond.postSort()), post.id.desc())
                .fetch();
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) // StringUtils.hasText(문자열) -> boolean 반환
                ? post.title.containsIgnoreCase(keyword) // true : 공백이 아닌 글자가 하나라도 있음
                .or(post.content.containsIgnoreCase(keyword)) // false : 공백이 아닌 글자가 없음 (null, "", "  ")
                : null; // where(...)에 null이 들어오면 QueryDSL이 조건 생략
    }

    private BooleanExpression writerNicknameEq(String nickname) {
        return StringUtils.hasText(nickname)
                ? post.writer.nickname.eq(nickname) : null;
    }

    // post.createdAt.desc()는 OrderSpecifier<LocalDateTime>이고,
    // postLike.count().desc()는 OrderSpecifier<Long>으로 분기마다 타입이 다르므로
    // 와일드카드 <?>를 적어주어야 함
    private OrderSpecifier<?> orderBy(PostSort sort) {
        if (sort == PostSort.LIKES) return postLike.count().desc();
        return post.createdAt.desc();
    }
}