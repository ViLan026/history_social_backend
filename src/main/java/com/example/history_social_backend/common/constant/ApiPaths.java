package com.example.history_social_backend.common.constant;

public final class ApiPaths {

    private ApiPaths() {
    }

    public static final String API_V1 = "/api/v1";

    
    // Auth
    public static final String AUTH = API_V1 + "/auth";


    // Users
    public static final String USERS = API_V1 + "/users";
    public static final String ROLES    = API_V1 + "/roles";
    // public static final String USER_PROFILE = USERS + "/profile";

    // Posts
    public static final String POSTS = API_V1 + "/posts";

    public static final String MEDIA = API_V1 + "/media";

    // Comments
    public static final String COMMENTS = API_V1 + "/comments";

    // Reactions
    public static final String REACTIONS = API_V1 + "/reactions";

    // Bookmarks
    public static final String BOOKMARKS = API_V1 + "/bookmarks";

    // Tags
    public static final String TAGS = API_V1 + "/tags";

    // Search
    public static final String SEARCH = API_V1 + "/search";

    // Notifications
    public static final String NOTIFICATIONS = API_V1 + "/notifications";

    // Reports
    public static final String REPORTS = API_V1 + "/reports";

    // Admin
    public static final String ADMIN = API_V1 + "/admin";
    public static final String ADMIN_USERS = ADMIN + "/users";
    public static final String ADMIN_REPORTS = ADMIN + "/reports";
}