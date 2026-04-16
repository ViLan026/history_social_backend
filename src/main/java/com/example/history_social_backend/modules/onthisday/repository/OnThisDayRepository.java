package com.example.history_social_backend.modules.onthisday.repository;

import com.example.history_social_backend.modules.onthisday.domain.OnThisDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnThisDayRepository extends JpaRepository<OnThisDay, java.util.UUID> {

  // Sử dụng EXTRACT của PostgreSQL để so sánh tháng/ngày (không quan tâm năm)

  @Query("""
      SELECT o FROM OnThisDay o
      WHERE EXTRACT(MONTH FROM o.eventDate) = :month
        AND EXTRACT(DAY FROM o.eventDate) = :day
      ORDER BY o.eventDate ASC
      """)
  List<OnThisDay> findEventsByMonthAndDay(@Param("month") int month, @Param("day") int day);
}