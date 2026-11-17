package efub.assignment.community.board.repositoriy;

import efub.assignment.community.board.domain.Board;

import java.util.List;

public interface CustomBoardRepository {

    List<Board> search(String keyword, String ownerNickname);
}
