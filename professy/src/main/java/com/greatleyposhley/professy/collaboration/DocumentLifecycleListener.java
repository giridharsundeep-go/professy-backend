package com.greatleyposhley.professy.collaboration;

import com.greatleyposhley.professy.entities.DocPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;

/** Keeps document revision metadata in step with JPA-managed document pages. */
@Component
public class DocumentLifecycleListener {
    @Autowired
    private JdbcTemplate jdbc;

    @PostPersist
    public void created(DocPage page) {
        jdbc.update("INSERT IGNORE INTO document_metadata(page_id,revision) VALUES(?,1)", page.getId());
    }

    @PostUpdate
    public void updated(DocPage page) {
        jdbc.update("INSERT INTO document_metadata(page_id,revision) VALUES(?,2) " +
                "ON DUPLICATE KEY UPDATE revision=revision+1", page.getId());
    }

    @PreRemove
    public void removing(DocPage page) {
        jdbc.update("DELETE FROM document_metadata WHERE page_id=?", page.getId());
        jdbc.update("DELETE FROM document_tags WHERE page_id=?", page.getId());
        jdbc.update("DELETE FROM document_revisions WHERE page_id=?", page.getId());
    }
}
