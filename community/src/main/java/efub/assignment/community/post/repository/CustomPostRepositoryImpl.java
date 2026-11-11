package efub.assignment.community.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import efub.assignment.community.board.domain.QBoard;
import efub.assignment.community.member.domain.QMember;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> search(String boardName, String writerName, String keyword) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        QBoard board = QBoard.board;

        BooleanBuilder builder = new BooleanBuilder();

        if (boardName != null && !boardName.isBlank()) {
            builder.and(post.board.boardname.containsIgnoreCase(boardName));
        }

        if (writerName != null && !writerName.isBlank()) {
            builder.and(post.writer.nickname.eq(writerName));
        }

        if (keyword != null && !keyword.isBlank()) {
            builder.and(post.content.containsIgnoreCase(keyword));
        }

        return jpaQueryFactory.selectFrom(post)
                .join(post.board, board).fetchJoin()
                .join(post.writer, member).fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}
