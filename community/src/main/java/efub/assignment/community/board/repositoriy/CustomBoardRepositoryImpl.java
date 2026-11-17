package efub.assignment.community.board.repositoriy;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.domain.QBoard;
import efub.assignment.community.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomBoardRepositoryImpl implements CustomBoardRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Board> search(String keyword, String ownerNickname) {
        QBoard board = QBoard.board;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();
        //게시판 주인 이름 확인
        if (ownerNickname != null && !ownerNickname.isBlank()) {
            builder.and(member.nickname.eq(ownerNickname));
        }
        //게시판 이름 확인
        if (keyword != null && !keyword.isBlank()) {
            builder.and(board.title.containsIgnoreCase(keyword));
        }

        return queryFactory.selectFrom(board)
                .join(board.boardOwner, member).fetchJoin()
                .where(builder)
                .orderBy(board.createdAt.desc())
                .fetch();
    }
}
