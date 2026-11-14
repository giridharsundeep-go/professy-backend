SET @migration_sql=IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='account_mail_queue' AND column_name='event_id'),'SELECT 1','ALTER TABLE account_mail_queue ADD COLUMN event_id VARCHAR(36), ADD COLUMN published_at TIMESTAMP(6) NULL');
PREPARE migration_stmt FROM @migration_sql;EXECUTE migration_stmt;DEALLOCATE PREPARE migration_stmt;
UPDATE account_mail_queue SET event_id=UUID() WHERE event_id IS NULL;
INSERT IGNORE INTO workspace_schema_migrations(version) VALUES('20261103_service_mail');
