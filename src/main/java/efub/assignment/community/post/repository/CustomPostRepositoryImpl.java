package efub.assignment.community.post.repository;

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

        // 작성자 닉네임이 있을 때만 조건 추가 (정확히 일치)
        // 익명 게시글은 작성자가 드러나지 않도록 작성자 검색 대상에서 제외
        if (writerNickname != null && !writerNickname.isBlank()) {
            builder.and(member.nickname.eq(writerNickname))
                    .and(post.isAnonymous.isFalse());
        }

        // 키워드가 있을 때만 조건 추가 (제목 또는 내용에 포함, 대소문자 무시)
        if (keyword != null && !keyword.isBlank()) {
            builder.and(post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword)));
        }

        return queryFactory.selectFrom(post)
                .join(post.writer, member).fetchJoin() // writer를 함께 조회해서 N+1 방지
                .where(builder)
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}
