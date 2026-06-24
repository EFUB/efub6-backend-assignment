package efub.assignment.community.message.repository;

import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    @Query("""
        SELECT COUNT(mr) > 0
        FROM MessageRoom mr
        WHERE mr.post = :post
          AND (
              (mr.sender = :sender AND mr.receiver = :receiver)
              OR
              (mr.sender = :receiver AND mr.receiver = :sender)
          )
    """)
    boolean existsByMembersAndPost(
            @Param("sender") Member sender,
            @Param("receiver") Member receiver,
            @Param("post") Post post
    );

    @Query("""
        SELECT mr
        FROM MessageRoom mr
        WHERE mr.post = :post
          AND (
              (mr.sender = :sender AND mr.receiver = :receiver)
              OR
              (mr.sender = :receiver AND mr.receiver = :sender)
          )
    """)
    Optional<MessageRoom> findByMembersAndPost(
            @Param("sender") Member sender,
            @Param("receiver") Member receiver,
            @Param("post") Post post
    );

    @Query("""
        SELECT DISTINCT mr
        FROM MessageRoom mr
        JOIN FETCH mr.sender
        JOIN FETCH mr.receiver
        JOIN FETCH mr.post
        JOIN FETCH mr.messages
        WHERE mr.sender = :member OR mr.receiver = :member
    """)
    List<MessageRoom> findAllByMemberFetchJoin(@Param("member") Member member);
}