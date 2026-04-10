package com.example.history_social_backend.common.constant;

public final class AppConstants {

    private AppConstants() {}

    // Roles 
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    // Pagination
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 50;

    // Sort
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // Security
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    // Upload
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // Cache (nếu dùng Redis sau này)
    public static final String CACHE_POST = "posts";
    public static final String CACHE_USER = "users";

}