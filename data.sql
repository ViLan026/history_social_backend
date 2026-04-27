
-- Seed data for history_social_backend
-- Run after schema is created.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- ------------------------------------------------------------
-- Core lookup data
-- ------------------------------------------------------------

INSERT INTO public.app_permissions (id, created_at, updated_at, description, name) VALUES
('11111111-1111-1111-1111-111111111111', NOW(), NOW(), 'Create new user', 'CREATE_USER'),
('11111111-1111-1111-1111-111111111112', NOW(), NOW(), 'Update user info', 'UPDATE_USER'),
('11111111-1111-1111-1111-111111111113', NOW(), NOW(), 'Delete user', 'DELETE_USER'),
('11111111-1111-1111-1111-111111111114', NOW(), NOW(), 'View user info', 'VIEW_USER'),
('11111111-1111-1111-1111-111111111115', NOW(), NOW(), 'Create post', 'CREATE_POST'),
('11111111-1111-1111-1111-111111111116', NOW(), NOW(), 'Delete post', 'DELETE_POST'),
('11111111-1111-1111-1111-111111111117', NOW(), NOW(), 'Moderate reports', 'MODERATE_REPORT'),
('11111111-1111-1111-1111-111111111118', NOW(), NOW(), 'Manage notifications', 'MANAGE_NOTIFICATION')
ON CONFLICT (name) DO NOTHING;

INSERT INTO public.roles (id, created_at, updated_at, description, name) VALUES
('22222222-2222-2222-2222-222222222221', NOW(), NOW(), 'Administrator', 'ADMIN'),
('22222222-2222-2222-2222-222222222222', NOW(), NOW(), 'Normal user', 'USER'),
('22222222-2222-2222-2222-222222222223', NOW(), NOW(), 'Moderator', 'MODERATOR')
ON CONFLICT (name) DO NOTHING;

-- ------------------------------------------------------------
-- Users + Profiles
-- ------------------------------------------------------------

INSERT INTO public.users (id, created_at, updated_at, email, password, status) VALUES
('33333333-3333-3333-3333-333333333331', NOW(), NOW(), 'admin@gmail.com',    '$2a$10$7QmVd3P5l8eQ5jv4JkzC9uXv1WbQm5l6fYxM8G7yW5eQKqjvQ9m7e', 'ACTIVE'),
('33333333-3333-3333-3333-333333333332', NOW(), NOW(), 'user@gmail.com',     '$2a$10$7QmVd3P5l8eQ5jv4JkzC9uXv1WbQm5l6fYxM8G7yW5eQKqjvQ9m7e', 'ACTIVE'),
('33333333-3333-3333-3333-333333333333', NOW(), NOW(), 'moderator@gmail.com','$2a$10$7QmVd3P5l8eQ5jv4JkzC9uXv1WbQm5l6fYxM8G7yW5eQKqjvQ9m7e', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.profiles (user_id, created_at, updated_at, avatar_url, bio, display_name, username) VALUES
('33333333-3333-3333-3333-333333333331', NOW(), NOW(), 'https://example.com/avatars/admin.png', 'System administrator', 'Admin System', 'admin01'),
('33333333-3333-3333-3333-333333333332', NOW(), NOW(), 'https://example.com/avatars/user.png', 'Normal account', 'Normal User', 'user01'),
('33333333-3333-3333-3333-333333333333', NOW(), NOW(), 'https://example.com/avatars/mod.png', 'Content moderator', 'Moderator User', 'mod01')
ON CONFLICT (username) DO NOTHING;

-- ------------------------------------------------------------
-- Role mappings
-- ------------------------------------------------------------

INSERT INTO public.user_roles (user_id, role_id) VALUES
('33333333-3333-3333-3333-333333333331', '22222222-2222-2222-2222-222222222221'),
('33333333-3333-3333-3333-333333333332', '22222222-2222-2222-2222-222222222222'),
('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222223')
ON CONFLICT DO NOTHING;

INSERT INTO public.role_permissions (role_id, permission_id) VALUES
('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111111'),
('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111112'),
('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111113'),
('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111114'),
('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111115'),
('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111116'),
('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111117'),
('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111118'),
('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111114'),
('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111115'),
('22222222-2222-2222-2222-222222222223', '11111111-1111-1111-1111-111111111114'),
('22222222-2222-2222-2222-222222222223', '11111111-1111-1111-1111-111111111115'),
('22222222-2222-2222-2222-222222222223', '11111111-1111-1111-1111-111111111117')
ON CONFLICT DO NOTHING;

-- ------------------------------------------------------------
-- Tags
-- ------------------------------------------------------------

INSERT INTO public.tags (id, created_at, updated_at, description, name, usage_count) VALUES
('44444444-4444-4444-4444-444444444441', NOW(), NOW(), 'Spring Boot tips', 'spring-boot', 2),
('44444444-4444-4444-4444-444444444442', NOW(), NOW(), 'PostgreSQL database', 'postgresql', 2),
('44444444-4444-4444-4444-444444444443', NOW(), NOW(), 'Java programming', 'java', 3),
('44444444-4444-4444-4444-444444444444', NOW(), NOW(), 'AI and machine learning', 'ai', 1)
ON CONFLICT (name) DO NOTHING;

-- ------------------------------------------------------------
-- Posts
-- ------------------------------------------------------------

INSERT INTO public.posts (id, created_at, updated_at, author_id, content, deleted_at, status, title, view_count) VALUES
('55555555-5555-5555-5555-555555555551', NOW(), NOW(), '33333333-3333-3333-3333-333333333331', 'Bai viet mo ta co ban ve Spring Boot va JWT.', NULL, 'PUBLISHED', 'Spring Boot JWT Guide', 125),
('55555555-5555-5555-5555-555555555552', NOW(), NOW(), '33333333-3333-3333-3333-333333333332', 'Huong dan toi uu PostgreSQL cho du an backend.', NULL, 'PUBLISHED', 'PostgreSQL Optimization Tips', 88),
('55555555-5555-5555-5555-555555555553', NOW(), NOW(), '33333333-3333-3333-3333-333333333333', 'Gioi thieu ve AI trong ung dung web hien dai.', NULL, 'DRAFT', 'AI in Modern Web Apps', 12)
ON CONFLICT DO NOTHING;

INSERT INTO public.posts (id, created_at, updated_at, author_id, content, deleted_at, status, title, view_count) VALUES
('55555555-5555-5555-5555-555555555555', NOW(), NOW(), '33333333-3333-3333-3333-333333333331', 'Bai viet mo ta co ban ve Spring Boot va JWT.', NULL, 'PUBLISHED', 'Spring Boot JWT Guide', 125),
('55555555-5555-5555-5555-555555555556', NOW(), NOW(), '33333333-3333-3333-3333-333333333332', 'Huong dan toi uu PostgreSQL cho du an backend.', NULL, 'PUBLISHED', 'PostgreSQL Optimization Tips', 88),
('55555555-5555-5555-5555-555555555554', NOW(), NOW(), '33333333-3333-3333-3333-333333333333', 'Gioi thieu ve AI trong ung dung web hien dai.', NULL, 'DRAFT', 'AI in Modern Web Apps', 12)
ON CONFLICT DO NOTHING;

INSERT INTO public.posts (id, created_at, updated_at, author_id, content, deleted_at, status, title, view_count) VALUES
('55555555-5555-5555-5555-555555555555', NOW(), NOW(), '33333333-3333-3333-3333-333333333331', '•	Providers.tsx: Đây là file bọc ngoài cùng ứng dụng (gọi trong app/layout.tsx). Nó sẽ khởi tạo QueryClientProvider của React Query, cấu hình Toaster (hiển thị thông báo), và bọc ThemeContext để ứng dụng hoạt động chuẩn xác.
•	ThemeContext.tsx: Quản lý giao diện Sáng/Tối.
8. Các file cấu hình hệ thống (Nằm ngoài cùng)
•	src/proxy.ts: Thường dùng để cấu hình proxy cho Next.js ở môi trường development, giúp gọi API qua localhost Spring Boot mà không bị lỗi CORS.
•	constants/api-endpoints.ts: Gom toàn bộ string URL API (/api/v1/posts) vào một chỗ để dễ đổi phiên bản API sau này.
•	tailwind.config.ts, postcss.config.mjs, next.config.ts: File cấu hình mặc định của framework.
•	package.json: Nơi khai báo các thư viện bạn đã cài (như axios, zustand, @tanstack/react-query).
Với cấu trúc này, luồng dữ liệu của bạn sẽ đi cực kỳ mượt mà: UI Component (Button/Page) -> Zustand (Cập nhật UI state) -> Feature Hook (React Query) -> Service (Axios) -> Backend.

', NULL, 'PUBLISHED', 'Spring Boot JWT Guide', 125),
('55555555-5555-5555-5555-555555555534', NOW(), NOW(), '33333333-3333-3333-3333-333333333332', 'layout.js sẽ bao bọc bên ngoài tất cả các file nằm cùng thư mục với nó và các thư mục con bên trong nó. Chữ {children} đơn giản chỉ là một "tọa độ" (placeholder) mà bạn đánh dấu để Next.js biết và thêm vào đó trang page.js 
•  ⏳ loading.js: Đây là giao diện chờ. Giả sử page.js của bạn cần 3 giây để lấy dữ liệu từ database. Thay vì để màn hình trắng bóc, Next.js sẽ tự động lấy file loading.js (ví dụ: vòng tròn xoay xoay) và nhét vào vị trí của children. Khi dữ liệu tải xong, nó rút loading.js ra và nhét page.js vào lại.
•  🚨 error.js: File này dùng để "bắt" lỗi. Nếu page.js của bạn bị lỗi code và sập, thay vì làm sập toàn bộ trang web (mất luôn Header, Footer), Next.js sẽ nhét file error.js vào vị trí của children để báo cho người dùng biết "Trang này đang bị lỗi", trong khi các phần khác của web vẫn hoạt động bình thường.
•  🔍 not-found.js: Giao diện lỗi 404. Sẽ hiện ra khi người dùng gõ sai đường dẫn.
•  🔄 template.js: Gần giống hệt layout.js, nhưng khác ở chỗ: mỗi khi bạn chuyển trang, template.js sẽ load lại từ đầu (thích hợp khi bạn muốn làm hiệu ứng animation chuyển trang), còn layout.js thì đứng im không load lại.

Dấu () bao quanh tên thư mục có thể hiểu  là một thư mục ẩn đối với browser: 
Lập trình viên có thể viết layout trong thư mục () còn layout ngoài thư mục thì sẽ bị ghi đè và tên thư mục trong () sẽ không hiển thị ở đường dẫn url 
.', NULL, 'PUBLISHED', 'PostgreSQL Optimization Tips', 88),
('55555555-5555-5555-5555-555555555567', NOW(), NOW(), '33333333-3333-3333-3333-333333333333', 'Gioi thieu ve AI trong ung dung web hien dai.', NULL, 'PUBLISHED', 'AI in Modern Web Apps', 12)
ON CONFLICT DO NOTHING;

INSERT INTO public.post_tags (created_at, post_id, tag_id) VALUES
(NOW(), '55555555-5555-5555-5555-555555555551', '44444444-4444-4444-4444-444444444441'),
(NOW(), '55555555-5555-5555-5555-555555555551', '44444444-4444-4444-4444-444444444443'),
(NOW(), '55555555-5555-5555-5555-555555555552', '44444444-4444-4444-4444-444444444442'),
(NOW(), '55555555-5555-5555-5555-555555555552', '44444444-4444-4444-4444-444444444443'),
(NOW(), '55555555-5555-5555-5555-555555555553', '44444444-4444-4444-4444-444444444444')
ON CONFLICT DO NOTHING;

-- ------------------------------------------------------------
-- Comments / Reactions / Bookmarks
-- ------------------------------------------------------------

INSERT INTO public."comments" (id, created_at, updated_at, content, deleted_at, author_id, post_id) VALUES
('66666666-6666-6666-6666-666666666661', NOW(), NOW(), 'Bai viet rat huu ich.', NULL, '33333333-3333-3333-3333-333333333332', '55555555-5555-5555-5555-555555555551'),
('66666666-6666-6666-6666-666666666662', NOW(), NOW(), 'Cam on ban da chia se.', NULL, '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555552')
ON CONFLICT DO NOTHING;

INSERT INTO public.reactions (id, created_at, "type", post_id, user_id) VALUES
('77777777-7777-7777-7777-777777777771', NOW(), 'LIKE', '55555555-5555-5555-5555-555555555551', '33333333-3333-3333-3333-333333333332'),
('77777777-7777-7777-7777-777777777772', NOW(), 'LOVE', '55555555-5555-5555-5555-555555555552', '33333333-3333-3333-3333-333333333331')
ON CONFLICT DO NOTHING;

INSERT INTO public.bookmarks (id, created_at, updated_at, post_id, user_id) VALUES
('88888888-8888-8888-8888-888888888881', NOW(), NOW(), '55555555-5555-5555-5555-555555555551', '33333333-3333-3333-3333-333333333332'),
('88888888-8888-8888-8888-888888888882', NOW(), NOW(), '55555555-5555-5555-5555-555555555552', '33333333-3333-3333-3333-333333333333')
ON CONFLICT DO NOTHING;

-- ------------------------------------------------------------
-- Post media / sources
-- ------------------------------------------------------------

INSERT INTO public.post_media (id, created_at, updated_at, display_order, media_type, media_url, public_id, resource_type, post_id) VALUES
('99999999-9999-9999-9999-999999999991', NOW(), NOW(), 1, 'IMAGE', 'https://example.com/media/springboot-cover.png', 'springboot-cover', 'image', '55555555-5555-5555-5555-555555555551'),
('99999999-9999-9999-9999-999999999992', NOW(), NOW(), 1, 'IMAGE', 'https://example.com/media/postgresql-cover.png', 'postgresql-cover', 'image', '55555555-5555-5555-5555-555555555552')
ON CONFLICT DO NOTHING;

INSERT INTO public.post_sources (id, author_name, published_year, title, url, post_id) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Spring Community', 2025, 'Spring Security Documentation', 'https://docs.spring.io/spring-security/reference/', '55555555-5555-5555-5555-555555555551'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'PostgreSQL Global Development Group', 2025, 'PostgreSQL Manual', 'https://www.postgresql.org/docs/', '55555555-5555-5555-5555-555555555552')
ON CONFLICT DO NOTHING;

-- ------------------------------------------------------------
-- Historical content
-- ------------------------------------------------------------

INSERT INTO public.on_this_day (id, created_at, updated_at, description, event_date, note) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', NOW(), NOW(), 'Python 1.0 was released.', DATE '1994-01-26', 'Sample historical event'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', NOW(), NOW(), 'JavaScript was announced.', DATE '1995-12-04', 'Sample historical event')
ON CONFLICT (event_date) DO NOTHING;

-- ------------------------------------------------------------
-- Reports / Notifications / Refresh tokens
-- ------------------------------------------------------------

INSERT INTO public.reports (id, created_at, reason, status, post_id, reporter_id) VALUES
('cccccccc-cccc-cccc-cccc-ccccccccccc1', NOW(), 'Noi dung khong phu hop', 'PENDING', '55555555-5555-5555-5555-555555555553', '33333333-3333-3333-3333-333333333332')
ON CONFLICT DO NOTHING;

INSERT INTO public.notifications (id, created_at, updated_at, actor_id, content, is_read, recipient_id, reference_id, "type") VALUES
('dddddddd-dddd-dddd-dddd-ddddddddddd1', NOW(), NOW(), '33333333-3333-3333-3333-333333333332', 'User nay da like bai viet cua ban.', FALSE, '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555551', 'LIKE'),
('dddddddd-dddd-dddd-dddd-ddddddddddd2', NOW(), NOW(), '33333333-3333-3333-3333-333333333333', 'Co mot binh luan moi.', FALSE, '33333333-3333-3333-3333-333333333331', '55555555-5555-5555-5555-555555555551', 'COMMENT'),
('dddddddd-dddd-dddd-dddd-ddddddddddd3', NOW(), NOW(), NULL, 'He thong da cap nhat du lieu mau.', TRUE, '33333333-3333-3333-3333-333333333332', NULL, 'SYSTEM')
ON CONFLICT DO NOTHING;

INSERT INTO public.refresh_token (id, created_at, updated_at, expiry_time, revoked, user_id) VALUES
('refresh-token-admin-001', NOW(), NOW(), NOW() + INTERVAL '7 days', FALSE, '33333333-3333-3333-3333-333333333331'),
('refresh-token-user-001', NOW(), NOW(), NOW() + INTERVAL '7 days', FALSE, '33333333-3333-3333-3333-333333333332')
ON CONFLICT (id) DO NOTHING;

COMMIT;