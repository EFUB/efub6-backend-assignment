package efub.assignment.community.messageroom.repository;

import efub.assignment.community.messageroom.domain.Message;
import efub.assignment.community.messageroom.domain.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByMessageRoomOrderByCreatedAtAsc(MessageRoom messageRoom);
}
