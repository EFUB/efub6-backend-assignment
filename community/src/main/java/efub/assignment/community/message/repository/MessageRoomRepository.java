package efub.assignment.community.message.repository;

import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    boolean existsBySenderAndReceiverAndPost(Member sender, Member receiver, Post post);

    Optional<MessageRoom> findBySenderAndReceiverAndPost(
            Member sender,
            Member receiver,
            Post post
    );

    List<MessageRoom> findAllBySenderOrReceiver(Member sender, Member receiver);
}