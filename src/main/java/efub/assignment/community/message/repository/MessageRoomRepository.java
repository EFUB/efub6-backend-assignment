package efub.assignment.community.message.repository;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.message.domain.MessageRoom;
import efub.assignment.community.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    // db에 날리는 쿼리 최소화를 위해 Query를 사용함.
    @Query("SELECT m FROM MessageRoom m WHERE m.post = :post AND " +
            "((m.sender = :member1 AND m.receiver = :member2) OR " +
            "(m.sender = :member2 AND m.receiver = :member1))")
    Optional<MessageRoom> findExistingMessageRoom(@Param("post") Post post,
                                               @Param("member1") Member member1,
                                               @Param("member2") Member member2);

    // sender or receiver로 참여 중인 모든 쪽지방을 최신순으로 조회
    @Query("SELECT m FROM MessageRoom m WHERE m.sender = :member OR m.receiver = :member ORDER BY m.createdAt DESC")
    List<MessageRoom> findAllBySenderOrReceiver(@Param("member") Member member);
}
