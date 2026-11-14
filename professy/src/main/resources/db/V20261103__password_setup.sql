SET @migration_sql=IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='user_account' AND column_name='password_setup_required'),'SELECT 1','ALTER TABLE user_account ADD COLUMN password_setup_required BIT NOT NULL DEFAULT 0');
PREPARE migration_stmt FROM @migration_sql; EXECUTE migration_stmt; DEALLOCATE PREPARE migration_stmt;
INSERT IGNORE INTO workspace_schema_migrations(version) VALUES('20261103_password_setup');
