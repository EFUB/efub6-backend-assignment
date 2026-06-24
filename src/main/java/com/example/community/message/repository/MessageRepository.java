package com.example.community.message.repository;

import com.example.community.message.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 쪽지방 내 모든 쪽지 오래된 순 조회
    List<Message> findAllByMessageRoomMessageRoomIdOrderByCreatedAtAsc(Long messageRoomId);

    // 쪽지방의 가장 최근 쪽지 1개 조회
    Optional<Message> findFirstByMessageRoomMessageRoomIdOrderByCreatedAtDesc(Long messageRoomId);

    // 여러 쪽지방의 최신 쪽지를 한 번에 조회
    @Query("""
            SELECT m FROM Message m
            WHERE m.messageRoom.messageRoomId IN :roomIds
            AND m.createdAt = (
                SELECT MAX(m2.createdAt) FROM Message m2
                WHERE m2.messageRoom.messageRoomId = m.messageRoom.messageRoomId
            )
            """)
    List<Message> findLatestMessagesForRooms(@Param("roomIds") List<Long> roomIds);
}
