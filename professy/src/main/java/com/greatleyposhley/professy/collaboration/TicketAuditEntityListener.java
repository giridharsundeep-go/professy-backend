package com.greatleyposhley.professy.collaboration;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/** Converts JPA ticket lifecycle events to the same audit/outbox records previously written by MySQL triggers. */
@Component
public class TicketAuditEntityListener {
    private static final Map<Object, Map<String, Object>> SNAPSHOTS =
            Collections.synchronizedMap(new WeakHashMap<>());

    @Autowired
    private TicketAuditService audit;

    @PostLoad
    public void loaded(Object entity) {
        SNAPSHOTS.put(entity, copy(audit.snapshot(entity)));
    }

    @PostPersist
    public void inserted(Object entity) {
        var after = audit.snapshot(entity);
        audit.recordEntity(entity, "CREATED", Map.of(), after);
        SNAPSHOTS.put(entity, copy(after));
    }

    @PostUpdate
    public void updated(Object entity) {
        var before = SNAPSHOTS.remove(entity);
        var after = audit.snapshot(entity);
        if (before == null || !before.equals(after)) {
            audit.recordEntity(entity, "UPDATED", before == null ? Map.of() : before, after);
        }
        SNAPSHOTS.put(entity, copy(after));
    }

    @PreRemove
    public void removing(Object entity) {
        var before = SNAPSHOTS.remove(entity);
        audit.recordEntity(entity, "DELETED", before == null ? audit.snapshot(entity) : before, Map.of());
    }

    private static Map<String, Object> copy(Map<String, Object> source) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }
}
