package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "document_tags", indexes = {@Index(name = "idx_document_tag", columnList = "tag,page_id")})
@IdClass(DocumentTags.Key.class)
@Entity
public class DocumentTags {
    @Id
    @Column(name = "page_id", nullable = false, columnDefinition = "BIGINT")
    private Long pageId;
    @Id
    @Column(name = "tag", nullable = false, columnDefinition = "VARCHAR(50)")
    private String tag;

    public static class Key implements Serializable {
        public Long pageId;
        public String tag;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(pageId, that.pageId) && Objects.equals(tag, that.tag);
        }
        @Override public int hashCode() { return Objects.hash(pageId, tag); }
    }

    protected DocumentTags() {}
}
