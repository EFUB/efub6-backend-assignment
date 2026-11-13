package efub.assignment.community.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.domain.QPost;
import efub.assignment.community.post.repository.CustomPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> search(String keyword, String authorNickname) {

        QPost post = QPost.post;

        BooleanBuilder builder = new BooleanBuilder();

        // 작성자 닉네임 검색
        if (authorNickname != null && !authorNickname.isBlank()) {
            builder.and(post.author.nickname.eq(authorNickname));
            builder.and(post.isAnonymous.isFalse());
        }

        // 게시글 내용 검색
        if (keyword != null && !keyword.isBlank()) {
            builder.and(post.content.containsIgnoreCase(keyword));
        }

        return queryFactory
                .selectFrom(post)
                .join(post.author).fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}