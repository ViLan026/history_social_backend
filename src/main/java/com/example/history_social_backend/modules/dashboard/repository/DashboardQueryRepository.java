package com.example.history_social_backend.modules.dashboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository tổng hợp query JPQL cho Admin Dashboard.
 * Chỉ đọc dữ liệu thống kê, không thao tác ghi.
 * Không phụ thuộc Spring Data Repository riêng để tránh ràng buộc single entity.
 */
@Repository
public class DashboardQueryRepository {

    @PersistenceContext
    private EntityManager em;

    // USER STATS

    public long countUsersByStatus(String status) {
        return em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    public long countAllUsers() {
        return em.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();
    }


    public List<Object[]> countNewUsersByDay(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "SELECT CAST(u.createdAt AS LocalDate), COUNT(u) " +
                        "FROM User u " +
                        "WHERE u.createdAt >= :from AND u.createdAt < :to " +
                        "GROUP BY CAST(u.createdAt AS LocalDate) " +
                        "ORDER BY CAST(u.createdAt AS LocalDate) ASC",
                        Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    // POST STATS

    public long countAllPosts() {
        return em.createQuery("SELECT COUNT(p) FROM Post p", Long.class)
                .getSingleResult();
    }

    public long countPostsByStatus(String status) {
        return em.createQuery(
                        "SELECT COUNT(p) FROM Post p WHERE p.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }


    public List<Object[]> countPostsGroupByStatus() {
        return em.createQuery(
                        "SELECT p.status, COUNT(p) FROM Post p GROUP BY p.status",
                        Object[].class)
                .getResultList();
    }

    public List<Object[]> countNewPostsByDay(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "SELECT CAST(p.createdAt AS LocalDate), COUNT(p) " +
                        "FROM Post p " +
                        "WHERE p.createdAt >= :from AND p.createdAt < :to " +
                        "GROUP BY CAST(p.createdAt AS LocalDate) " +
                        "ORDER BY CAST(p.createdAt AS LocalDate) ASC",
                        Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    // REPORT STATS

    public long countReportsByStatus(String status) {
        return em.createQuery(
                        "SELECT COUNT(r) FROM Report r WHERE r.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }


    public List<Object[]> countReportsGroupByStatus() {
        return em.createQuery(
                        "SELECT r.status, COUNT(r) FROM Report r GROUP BY r.status",
                        Object[].class)
                .getResultList();
    }


    public List<Object[]> countReportsGroupByReason() {
        return em.createQuery(
                        "SELECT r.reasonType, COUNT(r) FROM Report r GROUP BY r.reasonType",
                        Object[].class)
                .getResultList();
    }

     // Top bài viết bị report nhiều nhất.
    public List<Object[]> findTopReportedPosts(int limit) {
        return em.createQuery(
                        "SELECT p.id, p.title, p.author.id, p.status, p.qualityScore, p.createdAt, COUNT(r) " +
                        "FROM Post p JOIN Report r ON r.targetId = p.id " +
                        "WHERE r.targetType = 'POST' " +
                        "GROUP BY p.id, p.title, p.author.id, p.status, p.qualityScore, p.createdAt " +
                        "ORDER BY COUNT(r) DESC",
                        Object[].class)
                .setMaxResults(limit)
                .getResultList();
    }


     // Các report PENDING mới nhất, sắp xếp created_at DESC.

    public List<Object[]> findLatestPendingReports(int limit) {
        return em.createQuery(
                        "SELECT r.id, r.targetType, r.targetId, r.reasonType, r.reasonText, r.reporter.id, r.createdAt " +
                        "FROM Report r " +
                        "WHERE r.status = 'PENDING' " +
                        "ORDER BY r.createdAt DESC",
                        Object[].class)
                .setMaxResults(limit)
                .getResultList();
    }

    // COMMENT STATS

    public long countAllComments() {
        return em.createQuery("SELECT COUNT(c) FROM Comment c", Long.class)
                .getSingleResult();
    }

    public List<Object[]> countCommentsByDay(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "SELECT CAST(c.createdAt AS LocalDate), COUNT(c) " +
                        "FROM Comment c " +
                        "WHERE c.createdAt >= :from AND c.createdAt < :to " +
                        "GROUP BY CAST(c.createdAt AS LocalDate) " +
                        "ORDER BY CAST(c.createdAt AS LocalDate) ASC",
                        Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    // REACTION STATS

    public long countAllReactions() {
        return em.createQuery("SELECT COUNT(r) FROM Reaction r", Long.class)
                .getSingleResult();
    }

    /**
     * Trả về [reactionType, count] nhóm theo type của Reaction.
     */
    public List<Object[]> countReactionsGroupByType() {
        return em.createQuery(
                        "SELECT r.type, COUNT(r) FROM Reaction r GROUP BY r.type",
                        Object[].class)
                .getResultList();
    }

    public List<Object[]> countReactionsByDay(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "SELECT CAST(r.createdAt AS LocalDate), COUNT(r) " +
                        "FROM Reaction r " +
                        "WHERE r.createdAt >= :from AND r.createdAt < :to " +
                        "GROUP BY CAST(r.createdAt AS LocalDate) " +
                        "ORDER BY CAST(r.createdAt AS LocalDate) ASC",
                        Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    // BOOKMARK STATS
    public long countAllBookmarks() {
        return em.createQuery("SELECT COUNT(b) FROM Bookmark b", Long.class)
                .getSingleResult();
    }

    public List<Object[]> countBookmarksByDay(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "SELECT CAST(b.createdAt AS LocalDate), COUNT(b) " +
                        "FROM Bookmark b " +
                        "WHERE b.createdAt >= :from AND b.createdAt < :to " +
                        "GROUP BY CAST(b.createdAt AS LocalDate) " +
                        "ORDER BY CAST(b.createdAt AS LocalDate) ASC",
                        Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    // FOLLOW STATS
    public long countAllFollows() {
        return em.createQuery("SELECT COUNT(f) FROM Follow f", Long.class)
                .getSingleResult();
    }

    public List<Object[]> countFollowsByDay(LocalDateTime from, LocalDateTime to) {
        return em.createQuery(
                        "SELECT CAST(f.createdAt AS LocalDate), COUNT(f) " +
                        "FROM Follow f " +
                        "WHERE f.createdAt >= :from AND f.createdAt < :to " +
                        "GROUP BY CAST(f.createdAt AS LocalDate) " +
                        "ORDER BY CAST(f.createdAt AS LocalDate) ASC",
                        Object[].class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }


    public List<Object[]> findTopTags(int limit) {
        return em.createQuery(
                        "SELECT t.id, t.name, t.usageCount FROM Tag t ORDER BY t.usageCount DESC",
                        Object[].class)
                .setMaxResults(limit)
                .getResultList();
    }
}