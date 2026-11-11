package efub.assignment.community.post.repositoriy;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import efub.assignment.community.member.domain.QMember;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> search(String keyword, String writerNickname) {
        QPost post = QPost.post;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();
        if (writerNickname != null && !writerNickname.isBlank()){
            builder.and(member.nickname.eq(writerNickname));
        }
        if (keyword != null && !keyword.isBlank()) {
            builder.and(post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword)));
        }

        return queryFactory.selectFrom(post)
                .join(post.writer, member).fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}
