package efub.assignment.community.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostSearchCondition;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static efub.assignment.community.post.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> searchByConditions(Long boardId, PostSearchCondition condition) {
        return queryFactory
                .selectFrom(post)
                .where(
                        post.board.boardId.eq(boardId),
                        keywordContains(condition.keyword()),
                        writerContains(condition.writer())
                )
                .orderBy(post.createdAt.desc(), post.postId.desc())
                .fetch();
    }

    @Override
    public List<Post> searchByBoardIdAndKeyword(Long boardId, String keyword) {
        return searchByConditions(boardId, new PostSearchCondition(keyword, null));
    }

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null) {
            return null;
        }
        return post.title.containsIgnoreCase(keyword)
                .or(post.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression writerContains(String writer) {
        if (writer == null) {
            return null;
        }
        return post.anonymous.isFalse()
                .and(post.member.nickname.containsIgnoreCase(writer));
    }
}
