package com.example.community.messageroom.repository;

import com.example.community.messageroom.domain.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    // 특정 게시글에서 두 회원 사이의 쪽지방 조회
    @Query("""
            SELECT mr FROM MessageRoom mr
            WHERE mr.post.postId = :postId
            AND (
                (mr.sender.memberId = :memberId1 AND mr.receiver.memberId = :memberId2)
                OR (mr.sender.memberId = :memberId2 AND mr.receiver.memberId = :memberId1)
            )
            """)
    Optional<MessageRoom> findByPostAndParticipants(@Param("postId") Long postId,
                                                    @Param("memberId1") Long memberId1,
                                                    @Param("memberId2") Long memberId2);

    // 직접 메시지: post 없이 두 회원 간 쪽지방 조회
    @Query("""
            SELECT mr FROM MessageRoom mr
            WHERE mr.post IS NULL
            AND (
                (mr.sender.memberId = :memberId1 AND mr.receiver.memberId = :memberId2)
                OR (mr.sender.memberId = :memberId2 AND mr.receiver.memberId = :memberId1)
            )
            """)
    Optional<MessageRoom> findDirectRoomByParticipants(@Param("memberId1") Long memberId1,
                                                       @Param("memberId2") Long memberId2);

    // 특정 회원이 참여한 모든 쪽지방 조회
    @Query("""
            SELECT mr FROM MessageRoom mr
            LEFT JOIN FETCH mr.post
            JOIN FETCH mr.sender
            JOIN FETCH mr.receiver
            WHERE mr.sender.memberId = :memberId OR mr.receiver.memberId = :memberId
            """)
    List<MessageRoom> findAllByMemberIdWithDetails(@Param("memberId") Long memberId);
}
