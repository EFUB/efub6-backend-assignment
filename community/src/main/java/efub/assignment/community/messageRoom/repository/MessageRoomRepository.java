package efub.assignment.community.messageRoom.repository;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.messageRoom.domain.MessageRoom;
import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom,Long> {
    @Query("""
        SELECT mr FROM MessageRoom mr
        WHERE mr.post = :post
        AND (
            (mr.creator = :creator AND mr.participant = :participant)
            OR
            (mr.creator = :participant AND mr.participant = :creator)
        )
        """)
    Optional<MessageRoom> findByParticipantsAndPost(
            @Param("creator") Member creator,
            @Param("participant") Member participant,
            @Param("post") Post post
    );
    List<MessageRoom> findAllByCreatorOrParticipant(
            Member creator,
            Member participant
    );
}
