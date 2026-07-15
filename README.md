# History Social

Backend service for **History Social**, a social networking platform for sharing, discussing, and exploring Vietnamese historical content.

## Related Repositories

- Backend: https://github.com/ViLan026/history_social_backend
- Frontend: https://github.com/ViLan026/history-social-frontend
- AI Service: https://github.com/ViLan026/history_social_ai

## Project Overview

History Social is designed as a specialized social platform for history-related content. Users can create posts, attach sources, interact with other users, comment, follow accounts, bookmark posts, and report inappropriate content.

The system also integrates an AI pipeline to support historical content verification. When a user creates a historical post, the backend can send the content to the AI service, which extracts claims, retrieves related historical evidence, and returns a verification result.

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Maven
- Cloudinary

### External Services

- AI Service: FastAPI
- Vector Database: Qdrant
- LLM Serving: Ollama / Qwen
- Media Storage: Cloudinary

## System Architecture

The system follows a multi-service architecture:

```
Frontend (Next.js)
        |
        | REST API
        v
Backend (Spring Boot)
        |
        | PostgreSQL
        v
Relational Database
```

Backend also communicates with:

```
AI Service (FastAPI)
        |
        | Semantic Search
        v
Qdrant Vector Database
```

AI Service also uses:

Ollama / Qwen for claim extraction and verification

## Backend Structure

```text
src/main/java/com/example/history_social_backend
├── common
│   ├── constant
│   ├── domain
│   ├── response
│   └── utils
├── core
│   ├── configuration
│   ├── exception
│   └── security
└── modules
    ├── ai
    ├── auth
    ├── bookmark
    ├── comment
    ├── dashboard
    ├── follow
    ├── media
    ├── notification
    ├── onthisday
    ├── post
    ├── reaction
    ├── report
    └── user
```

### Common

The `common` package contains shared code used by many backend modules. It helps keep the project consistent and avoids duplicated logic.

#### constant

Stores fixed values used across the application, such as API paths and common application constants.

#### domain

Contains shared base classes, such as `BaseEntity`, for common fields like `createdAt` and `updatedAt`.

#### response

Defines common API response formats, such as `ApiResponse` and `PageResponse`, so the frontend receives data in a consistent structure.

#### utils

Contains helper classes for common tasks such as date-time handling, file processing, string processing, and UUID generation.

### Core

The `core` package contains the main technical setup of the backend, including configuration, error handling, and security support.

#### configuration

Contains application configuration classes for Spring Security, JWT, Cloudinary, async processing, and AI service communication.

#### exception

Handles application errors in a centralized way using custom exceptions, error codes, and global exception handling.

#### security

Contains helper logic for authentication and access control, such as getting the current authenticated user.

### Business Modules

The `modules` package contains the main business features of the application. Each module usually includes its own controller, service, repository, DTO, domain, and mapper classes.

#### ai

Connects the backend with the external AI service for historical fact-checking and hate-speech detection.

#### auth

Handles user registration, login, logout, JWT generation, refresh token management, and authentication logic.

#### bookmark

Manages saved posts, allowing users to bookmark and unbookmark historical posts.

#### comment

Handles comments and replies. It also supports checking comments with the hate-speech detection service.

#### dashboard

Provides statistics and overview data for the admin dashboard.

#### follow

Manages follow and unfollow actions between users.

#### media

Handles media upload and integration with Cloudinary for storing images, videos, or documents.

#### notification

Creates and manages notifications for user activities such as comments, replies, reactions, follows, reports, and fact-checking results.

#### onthisday

Manages historical events for the "On This Day" feature.

#### post

Handles post creation, post sources, tags, media, post status, feed display, and fact-checking results.

#### reaction

Manages user reactions to posts, such as like, love, sad, angry, or informative reactions.

#### report

Allows users to report posts or comments. Admin users can review, resolve, or dismiss reports.

#### user

Manages user accounts, profiles, roles, permissions, and user-related queries.

## Database

The backend uses PostgreSQL as the main relational database.

Main data groups include:

- Users and profiles
- Roles and permissions
- Refresh tokens
- Posts, tags, sources, and media
- Comments
- Reactions
- Bookmarks
- Follows
- Reports
- Notifications
- Historical fact-checking claims
- Hate-speech detection results
- On-this-day historical events

database: https://dbdiagram.io/d/PastConnext-69c8c223fb2db18e3b2acae3

## AI Integration Flow

The backend integrates with the AI service through the following flow:

```text
1. User creates a historical post.
2. Backend saves the post.
3. Backend sends post content to the AI service.
4. AI service extracts historical claims.
5. AI service retrieves evidence from Qdrant.
6. AI service verifies each claim.
7. Backend stores the fact-checking result.
8. User can view claim labels, explanations, and evidence.
```

Supported verification labels:

```text
SUPPORTED
REFUTED
NOT_ENOUGH_EVIDENCE
```

## Fact-checking Result Example

```json
{
    "claim": "Tran Hung Dao commanded the Dai Viet army in the Battle of Bach Dang in 1288.",
    "label": "SUPPORTED",
    "explanation": "The retrieved historical evidence supports the claim.",
    "evidence": [
        {
            "book_name": "Dai Viet Su Ky Toan Thu",
            "pages": [123, 124],
            "raw_text": "..."
        }
    ]
}
```

## Author

**Vi Thi Lan**
