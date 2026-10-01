package com.example.community.notification.repository;

import com.example.community.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 회원의 알림 목록 최신순 조회
    List<Notification> findAllByReceiverMemberIdOrderByCreatedAtDesc(Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.is_read = true " +
            "WHERE n.receiver.id = :memberId AND n.is_read = false")
    int markAllAsRead(@Param("memberId") Long memberId);
}
