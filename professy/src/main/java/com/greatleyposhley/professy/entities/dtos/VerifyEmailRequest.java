package com.greatleyposhley.professy.dtos;

public class VerifyEmailRequest {
    private String orgSlug;
    private String email;

    public VerifyEmailRequest() {}

    public VerifyEmailRequest(String orgSlug, String email) {
        this.orgSlug = orgSlug;
        this.email = email;
    }

    public String getOrgSlug() { return orgSlug; }
    public void setOrgSlug(String orgSlug) { this.orgSlug = orgSlug; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}