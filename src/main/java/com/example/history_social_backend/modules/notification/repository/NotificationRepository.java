package com.example.history_social_backend.modules.notification.repository;

import com.example.history_social_backend.modules.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Lấy danh sách thông báo của user, sắp xếp mới nhất trước
     */
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(
            UUID recipientId, Pageable pageable);

    /**
     * Đếm thông báo chưa đọc
     */
    @Query("""
            SELECT COUNT(n) FROM Notification n 
            WHERE n.recipientId = :recipientId 
            AND n.isRead = false
            """)
    long countUnreadByRecipientId(@Param("recipientId") UUID recipientId);

    /**
     * Đánh dấu tất cả thông báo của user là đã đọc (bulk update - tối ưu)
     */
    @Modifying
    @Query("""
            UPDATE Notification n 
            SET n.isRead = true 
            WHERE n.recipientId = :recipientId
            """)
    void markAllAsRead(@Param("recipientId") UUID recipientId);
}