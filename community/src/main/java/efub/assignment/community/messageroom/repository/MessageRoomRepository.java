package efub.assignment.community.messageroom.repository;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.messageroom.domain.MessageRoom;
import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    boolean existsByPostAndCreatorAndTarget(Post post, Member creator, Member target);
    List<MessageRoom> findAllByCreatorOrTarget(Member creator, Member target);
    @Query("SELECT m FROM MessageRoom m WHERE m.post = :post AND (m.creator = :requester OR m.target = :requester)")
    Optional<MessageRoom> findByPostAndMember(@Param("post")Post post, @Param("requester")Member requester);

}
