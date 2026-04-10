package com.example.history_social_backend.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Xử lý format, parse, timezone, “time ago”
public final class DateTimeUtils {

    private DateTimeUtils() {}

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    // ================= FORMAT =================

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DEFAULT_FORMATTER);
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    // ================= PARSE =================

    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return null;
        return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
    }

    // ================= NOW =================

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // ================= TIME AGO =================

    public static String timeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        Duration duration = Duration.between(dateTime, LocalDateTime.now());

        long seconds = duration.getSeconds();

        if (seconds < 60) return "just now";
        if (seconds < 3600) return (seconds / 60) + " minutes ago";
        if (seconds < 86400) return (seconds / 3600) + " hours ago";
        if (seconds < 2592000) return (seconds / 86400) + " days ago";
        if (seconds < 31536000) return (seconds / 2592000) + " months ago";

        return (seconds / 31536000) + " years ago";
    }

    // ================= CONVERT TIMEZONE =================

    public static LocalDateTime convertZone(LocalDateTime dateTime, ZoneId from, ZoneId to) {
        if (dateTime == null || from == null || to == null) return null;

        ZonedDateTime zoned = dateTime.atZone(from);
        return zoned.withZoneSameInstant(to).toLocalDateTime();
    }


    // ================= FORMAT =================

    public static String format(LocalDateTime time, String pattern, Locale locale) {
        if (time == null) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
        return time.format(formatter);
    }

    // ================= TIME AGO (i18n) =================

    public static String timeAgo(LocalDateTime time, String lang) {
        if (time == null) return "";

        Duration duration = Duration.between(time, LocalDateTime.now());
        long seconds = duration.getSeconds();

        boolean isVi = "vi".equalsIgnoreCase(lang);

        if (seconds < 60) {
            return isVi ? "vừa xong" : "just now";
        }

        if (seconds < 3600) {
            long minutes = seconds / 60;
            return isVi
                    ? minutes + " phút trước"
                    : minutes + " minutes ago";
        }

        if (seconds < 86400) {
            long hours = seconds / 3600;
            return isVi
                    ? hours + " giờ trước"
                    : hours + " hours ago";
        }

        if (seconds < 2592000) {
            long days = seconds / 86400;
            return isVi
                    ? days + " ngày trước"
                    : days + " days ago";
        }

        long months = seconds / 2592000;
        return isVi
                ? months + " tháng trước"
                : months + " months ago";
    }

}