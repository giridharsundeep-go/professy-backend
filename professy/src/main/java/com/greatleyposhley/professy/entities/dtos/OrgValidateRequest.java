package com.greatleyposhley.professy.entities.dtos;

public class OrgValidateRequest {

    private String slug;

    public OrgValidateRequest() {
    }

    public OrgValidateRequest(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}