CREATE TABLE IF NOT EXISTS document_metadata(page_id BIGINT PRIMARY KEY,revision BIGINT NOT NULL DEFAULT 1);
CREATE TABLE IF NOT EXISTS document_tags(page_id BIGINT NOT NULL,tag VARCHAR(50) NOT NULL,PRIMARY KEY(page_id,tag),KEY idx_document_tag(tag,page_id));
CREATE TABLE IF NOT EXISTS document_revisions(id BIGINT AUTO_INCREMENT PRIMARY KEY,page_id BIGINT NOT NULL,revision BIGINT NOT NULL,title VARCHAR(255) NOT NULL,content LONGTEXT NULL,saved_by BIGINT NOT NULL,created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),UNIQUE KEY uk_document_revision(page_id,revision));
INSERT IGNORE INTO document_metadata(page_id) SELECT id FROM doc_pages;
DELIMITER $$
DROP TRIGGER IF EXISTS gp_document_revision_insert$$
CREATE TRIGGER gp_document_revision_insert AFTER INSERT ON doc_pages FOR EACH ROW BEGIN INSERT IGNORE INTO document_metadata(page_id) VALUES(NEW.id); END$$
DROP TRIGGER IF EXISTS gp_document_revision_update$$
CREATE TRIGGER gp_document_revision_update AFTER UPDATE ON doc_pages FOR EACH ROW BEGIN INSERT INTO document_metadata(page_id,revision) VALUES(NEW.id,2) ON DUPLICATE KEY UPDATE revision=revision+1; END$$
DROP TRIGGER IF EXISTS gp_document_metadata_delete$$
CREATE TRIGGER gp_document_metadata_delete AFTER DELETE ON doc_pages FOR EACH ROW BEGIN DELETE FROM document_metadata WHERE page_id=OLD.id;DELETE FROM document_tags WHERE page_id=OLD.id;DELETE FROM document_revisions WHERE page_id=OLD.id; END$$
DELIMITER ;
INSERT IGNORE INTO workspace_schema_migrations(version) VALUES('20261103_document_workspace');
