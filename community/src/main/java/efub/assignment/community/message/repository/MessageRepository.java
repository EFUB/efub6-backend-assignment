package efub.assignment.community.message.repository;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.message.domain.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findTopByMessageRoomOrderByCreatedAtDesc(MessageRoom messageRoom);

    List<Message> findAllByMessageRoomOrderByCreatedAtAsc(MessageRoom messageRoom);

    void deleteAllByMessageRoom(MessageRoom messageRoom);
}