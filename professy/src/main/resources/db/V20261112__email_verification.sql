SET @migration_sql=IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='user_account' AND column_name='email_verified'),'SELECT 1','ALTER TABLE user_account ADD COLUMN email_verified BIT NOT NULL DEFAULT 0');
PREPARE verification_stmt FROM @migration_sql; EXECUTE verification_stmt; DEALLOCATE PREPARE verification_stmt;
CREATE TABLE IF NOT EXISTS email_verification_tokens (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL,
 token_hash CHAR(64) NOT NULL UNIQUE, expires_at TIMESTAMP(6) NOT NULL,
 used_at TIMESTAMP(6) NULL, created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 INDEX ix_email_verification_user (user_id,expires_at)
);
