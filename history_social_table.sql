CREATE TABLE users (
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	id uuid NOT NULL,
	status varchar(20) NOT NULL,
	email varchar(255) NOT NULL,
	"password" varchar(255) NOT NULL,
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying])::text[])))
);


CREATE TABLE profiles (
	created_at timestamp(6) NOT NULL,
	follower_count int8 NOT NULL,
	following_count int8 NOT NULL,
	updated_at timestamp(6) NULL,
	user_id uuid NOT NULL,
	display_name varchar(100) NULL,
	username varchar(100) NOT NULL,
	avatar_url varchar(255) NULL,
	bio text NULL,
	CONSTRAINT profiles_pkey PRIMARY KEY (user_id),
	CONSTRAINT profiles_username_key UNIQUE (username),
	CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id)
);



CREATE TABLE roles (
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	id uuid NOT NULL,
	"name" varchar(50) NOT NULL,
	description varchar(255) NULL,
	CONSTRAINT roles_name_key UNIQUE (name),
	CONSTRAINT roles_pkey PRIMARY KEY (id)
);



CREATE TABLE app_permissions (
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	id uuid NOT NULL,
	"name" varchar(50) NOT NULL,
	description varchar(255) NULL,
	CONSTRAINT app_permissions_name_key UNIQUE (name),
	CONSTRAINT app_permissions_pkey PRIMARY KEY (id)
);



CREATE TABLE user_roles (
	user_id uuid NOT NULL,
	role_id uuid NOT NULL,
	CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
	CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id),
	CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE role_permissions (
	role_id uuid NOT NULL,
	permission_id uuid NOT NULL,
	CONSTRAINT role_permissions_pkey PRIMARY KEY (role_id, permission_id),
	CONSTRAINT fklcogaoj9yxsyof64a420c8qrq FOREIGN KEY (permission_id) REFERENCES app_permissions(id),
	CONSTRAINT fkn5fotdgk8d1xvo8nav9uv3muc FOREIGN KEY (role_id) REFERENCES roles(id)
);


CREATE TABLE refresh_token (
	id varchar(255) NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	expiry_time timestamp(6) NOT NULL,
	revoked bool NOT NULL,
	user_id uuid NOT NULL,
	CONSTRAINT refresh_token_pkey PRIMARY KEY (id),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);



CREATE TABLE posts (
	quality_score float8 NOT NULL,
	bookmark_count int8 NOT NULL,
	comment_count int8 NOT NULL,
	created_at timestamp(6) NOT NULL,
	deleted_at timestamp(6) NULL,
	reaction_count int8 NOT NULL,
	report_count int8 NOT NULL,
	updated_at timestamp(6) NULL,
	author_id uuid NOT NULL,
	id uuid NOT NULL,
	status varchar(20) NOT NULL,
	title varchar(500) NOT NULL,
	"content" text NOT NULL,
	CONSTRAINT posts_pkey PRIMARY KEY (id),
	CONSTRAINT posts_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PUBLISHED'::character varying, 'HIDDEN'::character varying, 'FLAGGED'::character varying, 'REJECTED'::character varying])::text[]))),
    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_post_author ON public.posts USING btree (author_id);
CREATE INDEX idx_post_created ON public.posts USING btree (id);
CREATE INDEX idx_post_status ON public.posts USING btree (status);



CREATE TABLE tags (
	usage_count int4 NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	id uuid NOT NULL,
	"name" varchar(100) NOT NULL,
	description varchar(300) NULL,
	CONSTRAINT tags_name_key UNIQUE (name),
	CONSTRAINT tags_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_tag_name ON public.tags USING btree (name);



CREATE TABLE post_media (
	display_order int4 NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	id uuid NOT NULL,
	post_id uuid NOT NULL,
	media_type varchar(20) NOT NULL,
	resource_type varchar(20) NOT NULL,
	public_id varchar(500) NOT NULL,
	media_url varchar(1000) NOT NULL,
	CONSTRAINT post_media_media_type_check CHECK (((media_type)::text = ANY ((ARRAY['IMAGE'::character varying, 'VIDEO'::character varying, 'DOCUMENT'::character varying])::text[]))),
	CONSTRAINT post_media_pkey PRIMARY KEY (id),
	CONSTRAINT fk1urcum9dtf0vgul7k405f4r2d FOREIGN KEY (post_id) REFERENCES posts(id)
);



CREATE TABLE post_sources (
	published_year int4 NULL,
	id uuid NOT NULL,
	post_id uuid NOT NULL,
	author_name varchar(300) NULL,
	title varchar(500) NOT NULL,
	url varchar(1000) NULL,
	CONSTRAINT post_sources_pkey PRIMARY KEY (id),
	CONSTRAINT fk289rw3fxvdo8cstdo7r7ytclf FOREIGN KEY (post_id) REFERENCES posts(id)
);



CREATE TABLE post_tags (
	created_at timestamp(6) NULL,
	post_id uuid NOT NULL,
	tag_id uuid NOT NULL,
	CONSTRAINT post_tags_pkey PRIMARY KEY (post_id, tag_id),
	CONSTRAINT fkkifam22p4s1nm3bkmp1igcn5w FOREIGN KEY (post_id) REFERENCES posts(id),
	CONSTRAINT fkm6cfovkyqvu5rlm6ahdx3eavj FOREIGN KEY (tag_id) REFERENCES tags(id)
);



CREATE TABLE follows (
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	follower_id uuid NOT NULL,
	following_id uuid NOT NULL,
	id uuid NOT NULL,
	CONSTRAINT follows_follower_id_following_id_key UNIQUE (follower_id, following_id),
	CONSTRAINT follows_pkey PRIMARY KEY (id),
	CONSTRAINT chk_follow_self CHECK (follower_id <> following_id),
    CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id)  REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_follows_following FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE bookmarks (
	id uuid NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	post_id uuid NOT NULL,
	user_id uuid NOT NULL,
	CONSTRAINT bookmarks_pkey PRIMARY KEY (id),
	CONSTRAINT bookmarks_post_id_user_id_key UNIQUE (post_id, user_id),
    CONSTRAINT fk_bookmarks_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE, 
    CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE

);


CREATE TABLE "comments" (
	created_at timestamp(6) NOT NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	author_id uuid NOT NULL,
	id uuid NOT NULL,
	parent_id uuid NULL,
	post_id uuid NOT NULL,
	"content" text NOT NULL,
	CONSTRAINT comments_pkey PRIMARY KEY (id),
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE, 
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id), 
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
);



CREATE TABLE notifications (
	is_read bool NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	actor_id uuid NULL,
	id uuid NOT NULL,
	recipient_id uuid NOT NULL,
	reference_id uuid NULL,
	"content" text NULL,
	"type" varchar(255) NOT NULL,
	CONSTRAINT notifications_pkey PRIMARY KEY (id),
	CONSTRAINT notifications_type_check CHECK (((type)::text = ANY ((ARRAY['LIKE'::character varying, 'COMMENT'::character varying, 'FOLLOW'::character varying, 'SYSTEM'::character varying])::text[]))),
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE, 
    CONSTRAINT fk_notifications_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE SET NULL

);



CREATE TABLE on_this_day (
	id uuid NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	description text NOT NULL,
	event_date date NOT NULL,
	title text NULL,
	CONSTRAINT on_this_day_pkey PRIMARY KEY (id),
	CONSTRAINT on_this_day_event_date_key UNIQUE (event_date)
);



CREATE TABLE reactions (
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	id uuid NOT NULL,
	post_id uuid NOT NULL,
	user_id uuid NOT NULL,
	"type" varchar(255) NOT NULL,
	CONSTRAINT reactions_pkey PRIMARY KEY (id),
	CONSTRAINT reactions_type_check CHECK (((type)::text = ANY ((ARRAY['INFORMATIVE'::character varying, 'SURPRISED'::character varying, 'SAD'::character varying, 'LIKE'::character varying, 'LOVE'::character varying, 'ANGRY'::character varying])::text[]))),
	CONSTRAINT reactions_post_id_user_id_key UNIQUE (post_id, user_id),
    CONSTRAINT fk_reactions_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_reactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);




CREATE TABLE reports (
	id uuid NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	reason_text text NULL,
	reason_type varchar(50) NOT NULL,
	reporter_id uuid NOT NULL,
	reviewed_at timestamp(6) NULL,
	reviewed_by uuid NULL,
	status varchar(30) NOT NULL,
	target_id uuid NOT NULL,
	target_type varchar(30) NOT NULL,
	CONSTRAINT reports_pkey PRIMARY KEY (id),
	CONSTRAINT reports_reason_type_check CHECK (((reason_type)::text = ANY ((ARRAY['MISINFORMATION'::character varying, 'FAKE_HISTORY'::character varying, 'HATE_SPEECH'::character varying, 'VIOLENCE'::character varying, 'HARASSMENT'::character varying, 'SPAM'::character varying, 'INAPPROPRIATE'::character varying, 'OTHER'::character varying])::text[]))),
	CONSTRAINT reports_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'RESOLVED'::character varying, 'DISMISSED'::character varying])::text[]))),
	CONSTRAINT reports_target_type_check CHECK (((target_type)::text = ANY ((ARRAY['POST'::character varying, 'COMMENT'::character varying])::text[]))),
    CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users(id), 
    CONSTRAINT fk_reports_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id)
);

