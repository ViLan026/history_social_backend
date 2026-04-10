package com.example.history_social_backend.modules.onthisday.repository;


import com.example.history_social_backend.modules.onthisday.domain.OnThisDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnThisDayRepository extends JpaRepository<OnThisDay, java.util.UUID> {

    /**
     * FR-13: Custom query lấy sự kiện "Ngày này năm xưa"
     * Sử dụng EXTRACT của PostgreSQL để so sánh tháng/ngày (không quan tâm năm)
     * Trả về theo thứ tự năm tăng dần để hiển thị logic lịch sử
     */
    @Query("""
            SELECT o FROM OnThisDay o 
            WHERE EXTRACT(MONTH FROM o.eventDate) = :month 
              AND EXTRACT(DAY FROM o.eventDate) = :day 
            ORDER BY o.eventDate ASC
            """)
    List<OnThisDay> findEventsByMonthAndDay(@Param("month") int month, @Param("day") int day);
}