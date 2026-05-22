package efub.assignment.community.message.repository;

import efub.assignment.community.message.domain.Message;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 최신 메시지 가져오기
    Optional<Message> findTopByMessageRoomOrderByCreatedAtDesc(MessageRoom messageRoom);

    List<Message> findAllByMessageRoom(MessageRoom messageRoom);
}
