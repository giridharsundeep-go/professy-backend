package com.greatleyposhley.professy.entities.dtos;

public class OrgValidateResponse {

    private boolean valid;
    private Long id;
    private String title;

    public OrgValidateResponse() {
    }

    public OrgValidateResponse(boolean valid, Long id, String title) {
        this.valid = valid;
        this.id = id;
        this.title = title;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}