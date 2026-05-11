package efub.assignment.community.board.repositoriy;


import efub.assignment.community.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
