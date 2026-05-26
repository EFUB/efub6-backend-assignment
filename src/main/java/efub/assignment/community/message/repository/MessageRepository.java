package efub.assignment.community.message.repository;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.message.dto.summary.MessageSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m " +
            "JOIN FETCH m.sender " +
            "WHERE m.messageRoom = :messageRoom " +
            "ORDER BY m.createdAt DESC")
    List<Message> findTop1ByMessageRoomWithFetchJoin(@Param("messageRoom") MessageRoom messageRoom);
}
