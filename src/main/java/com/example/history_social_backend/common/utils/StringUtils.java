package com.example.history_social_backend.common.utils;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;


// Xử lý string phổ biến (validate, slug, sanitize)
public final class StringUtils {

    private StringUtils() {}

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    // ================= BASIC =================

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    // ================= SLUG (SEO / URL) =================

    public static String toSlug(String input) {
        if (input == null) return null;

        String noAccent = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        String slug = WHITESPACE.matcher(noAccent).replaceAll("-");
        slug = NON_LATIN.matcher(slug).replaceAll("");

        return slug.toLowerCase();
    }

    // ================= RANDOM STRING =================

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    // ================= LIMIT LENGTH =================

    public static String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;

        return str.substring(0, maxLength) + "...";
    }

    // ================= SANITIZE =================

    public static String safe(String str) {
        if (str == null) return "";
        return str.replaceAll("<", "&lt;")
                  .replaceAll(">", "&gt;");
    }
}