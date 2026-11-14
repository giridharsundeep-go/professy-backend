-- Additive migration: retains all existing users, tickets and test cases.
CREATE TABLE IF NOT EXISTS workspace_schema_migrations (version VARCHAR(100) PRIMARY KEY, applied_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6));
CREATE TABLE IF NOT EXISTS ticket_comments (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, org_id BIGINT NOT NULL, project_id BIGINT NOT NULL,
 ticket_type VARCHAR(20) NOT NULL, ticket_id BIGINT NOT NULL, author_id BIGINT NOT NULL,
 content TEXT NOT NULL, created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), deleted_at TIMESTAMP(6) NULL,
 INDEX ix_comments_ticket (org_id,ticket_type,ticket_id,id)
);
CREATE TABLE IF NOT EXISTS ticket_attachments (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, org_id BIGINT NOT NULL, project_id BIGINT NOT NULL,
 ticket_type VARCHAR(20) NOT NULL, ticket_id BIGINT NOT NULL, uploaded_by BIGINT NOT NULL,
 original_name VARCHAR(200) NOT NULL, storage_name VARCHAR(255) NOT NULL UNIQUE,
 content_type VARCHAR(150) NOT NULL, byte_size BIGINT NOT NULL,
 created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), deleted_at TIMESTAMP(6) NULL,
 INDEX ix_attachments_ticket (org_id,ticket_type,ticket_id,id)
);
CREATE TABLE IF NOT EXISTS ticket_watchers (
 org_id BIGINT NOT NULL, project_id BIGINT NOT NULL, ticket_type VARCHAR(20) NOT NULL,
 ticket_id BIGINT NOT NULL, user_id BIGINT NOT NULL,
 PRIMARY KEY (org_id,ticket_type,ticket_id,user_id)
);
CREATE TABLE IF NOT EXISTS ticket_activity (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, event_id CHAR(36) NOT NULL UNIQUE,
 org_id BIGINT NOT NULL, project_id BIGINT NOT NULL, ticket_type VARCHAR(20) NOT NULL,
 ticket_id BIGINT NOT NULL, actor_id BIGINT NOT NULL, event_type VARCHAR(50) NOT NULL,
 summary VARCHAR(500) NOT NULL, changes_json JSON NOT NULL,
 created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 INDEX ix_activity_ticket (org_id,ticket_type,ticket_id,id)
);
CREATE TABLE IF NOT EXISTS ticket_event_outbox (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, event_id CHAR(36) NOT NULL UNIQUE,
 aggregate_key VARCHAR(100) NOT NULL, payload JSON NOT NULL,
 created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), published_at TIMESTAMP(6) NULL,
 attempts INT NOT NULL DEFAULT 0, next_attempt_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 INDEX ix_outbox_pending (published_at,next_attempt_at,id)
);
CREATE TABLE IF NOT EXISTS user_notifications (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, event_id CHAR(36) NOT NULL,
 user_id BIGINT NOT NULL, org_id BIGINT NOT NULL, project_id BIGINT NOT NULL,
 ticket_type VARCHAR(20) NOT NULL, ticket_id BIGINT NOT NULL,
 title VARCHAR(300) NOT NULL, body VARCHAR(600) NOT NULL,
 created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), read_at TIMESTAMP(6) NULL,
 UNIQUE KEY uk_notification_event_user (event_id,user_id), INDEX ix_user_unread (user_id,read_at,id)
);
CREATE TABLE IF NOT EXISTS password_setup_tokens (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL,
 token_hash CHAR(64) NOT NULL UNIQUE, expires_at TIMESTAMP(6) NOT NULL,
 used_at TIMESTAMP(6) NULL, created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 INDEX ix_setup_user (user_id,expires_at)
);
CREATE TABLE IF NOT EXISTS account_mail_queue (
 user_id BIGINT PRIMARY KEY, reason VARCHAR(30) NOT NULL,
 requested_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), sent_at TIMESTAMP(6) NULL,
 attempts INT NOT NULL DEFAULT 0, next_attempt_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);
CREATE TABLE IF NOT EXISTS auth_request_limits (
 request_key CHAR(64) PRIMARY KEY, attempts INT NOT NULL,
 expires_at TIMESTAMP(6) NOT NULL
);
SET @workspace_ddl = IF((SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='user_account' AND column_name='credential_version')=0,
 'ALTER TABLE user_account ADD COLUMN credential_version INT NOT NULL DEFAULT 0','DO 0');
PREPARE workspace_stmt FROM @workspace_ddl; EXECUTE workspace_stmt; DEALLOCATE PREPARE workspace_stmt;
INSERT IGNORE INTO workspace_schema_migrations(version) VALUES ('20261103_ticket_collaboration');
