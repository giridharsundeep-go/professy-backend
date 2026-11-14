package com.greatleyposhley.professy.collaboration;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
/** Legacy document APIs enforce the same per-document sharing as Docsy. */
@Service public class DocumentAccess {
 private final WorkAccess access;public DocumentAccess(WorkAccess access){this.access=access;}
 public Set<Long> readable(){access.require("DOCUMENTS.READ");var actor=access.actor();return new HashSet<>(access.jdbc.queryForList("SELECT p.id FROM doc_pages p LEFT JOIN document_sharing ds ON ds.page_id=p.id WHERE p.org_id=? AND (p.created_by=? OR EXISTS(SELECT 1 FROM document_shares sh WHERE sh.page_id=p.id AND sh.user_id=?))",Long.class,actor.getOrgId(),actor.getId(),actor.getId()));}
 public void require(Long org,Long id,boolean edit){var actor=access.actor();if(!Objects.equals(org,actor.getOrgId())||!readable().contains(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Document not found or not shared with you");if(!edit)return;access.require("DOCUMENTS.UPDATE");if(access.jdbc.queryForObject("SELECT COUNT(*) FROM doc_pages p LEFT JOIN document_sharing ds ON ds.page_id=p.id WHERE p.id=? AND p.org_id=? AND (p.created_by=? OR EXISTS(SELECT 1 FROM document_shares sh WHERE sh.page_id=p.id AND sh.user_id=? AND sh.permission='EDITOR'))",Integer.class,id,org,actor.getId(),actor.getId())==0)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Editing is not allowed for this document");}
}
