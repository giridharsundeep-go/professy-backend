package com.greatleyposhley.professy.collaboration;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.security.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class PasswordSetupService {
    private final WorkAccess access;
    private final PasswordEncoder passwords;
    private final String uiUrl;
    public PasswordSetupService(WorkAccess access,PasswordEncoder passwords,@Value("${app.ui-url:http://localhost:4200}")String uiUrl){this.access=access;this.passwords=passwords;this.uiUrl=uiUrl;}

    public void queue(long userId,String reason){access.jdbc.update("INSERT INTO account_mail_queue(user_id,reason,event_id,requested_at,sent_at,published_at,attempts,next_attempt_at) VALUES(?,?,?,CURRENT_TIMESTAMP,NULL,NULL,0,CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE reason=VALUES(reason),event_id=VALUES(event_id),requested_at=CURRENT_TIMESTAMP,sent_at=NULL,published_at=NULL,attempts=0,next_attempt_at=CURRENT_TIMESTAMP",userId,reason,UUID.randomUUID().toString());}
    public void cancelVerificationMail(long userId){access.jdbc.update("DELETE FROM account_mail_queue WHERE user_id=? AND reason=? AND sent_at IS NULL",userId,"EMAIL_VERIFICATION");}
    @Transactional
    public String link(long userId){
        // Callers must authorize admin access, or call this only from the mail worker.
        byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);String token=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        access.jdbc.update("UPDATE password_setup_tokens SET used_at=CURRENT_TIMESTAMP WHERE user_id=? AND used_at IS NULL",userId);
        access.jdbc.update("INSERT INTO password_setup_tokens(user_id,token_hash,expires_at) VALUES(?,?,TIMESTAMPADD(HOUR,24,CURRENT_TIMESTAMP))",userId,hash(token));
        return uiUrl.replaceAll("/$","")+"/reset-password?token="+token;
    }
    @Transactional
    public String verificationLink(long userId){
        byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);String token=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        access.jdbc.update("UPDATE email_verification_tokens SET used_at=CURRENT_TIMESTAMP WHERE user_id=? AND used_at IS NULL",userId);
        access.jdbc.update("INSERT INTO email_verification_tokens(user_id,token_hash,expires_at) VALUES(?,?,TIMESTAMPADD(HOUR,24,CURRENT_TIMESTAMP))",userId,hash(token));
        return uiUrl.replaceAll("/$","")+"/verify-email?token="+token;
    }
    @Transactional
    public String verifyEmail(String token){
        if(token==null||token.length()>200)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This verification link is invalid or has expired");
        var rows=access.jdbc.queryForList("SELECT t.id,t.user_id,(u.password_setup_required+0) AS setup_required FROM email_verification_tokens t JOIN user_account u ON u.id=t.user_id WHERE t.token_hash=? AND t.used_at IS NULL AND t.expires_at>CURRENT_TIMESTAMP AND u.is_active=1 FOR UPDATE",hash(token));
        if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This verification link is invalid or has expired");
        long userId=WorkAccess.number(rows.getFirst().get("user_id"));
        access.jdbc.update("UPDATE email_verification_tokens SET used_at=CURRENT_TIMESTAMP WHERE user_id=? AND used_at IS NULL",userId);
        access.jdbc.update("UPDATE user_account SET email_verified=1 WHERE id=?",userId);
        return WorkAccess.number(rows.getFirst().get("setup_required"))>0?link(userId):null;
    }
    @Transactional
    public void request(String email,String address){
        String normalized=email==null?"":email.trim().toLowerCase(Locale.ROOT);
        String key=hash(address+"|"+normalized);
        access.jdbc.update("INSERT INTO auth_request_limits(request_key,attempts,expires_at) VALUES(?,1,TIMESTAMPADD(MINUTE,15,CURRENT_TIMESTAMP)) ON DUPLICATE KEY UPDATE attempts=IF(expires_at<CURRENT_TIMESTAMP,1,attempts+1),expires_at=IF(expires_at<CURRENT_TIMESTAMP,TIMESTAMPADD(MINUTE,15,CURRENT_TIMESTAMP),expires_at)",key);
        if(access.jdbc.queryForObject("SELECT attempts FROM auth_request_limits WHERE request_key=?",Integer.class,key)>5)return;
        var users=access.jdbc.queryForList("SELECT id FROM user_account WHERE LOWER(email)=? AND is_active=1",Long.class,normalized);
        if(!users.isEmpty())queue(users.getFirst(),"PASSWORD_RESET");
    }
    @Transactional
    public void reset(String token,String password){
        validatePassword(password);
        if(token==null||token.length()>200)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This password link is invalid or has expired");
        var rows=access.jdbc.queryForList("SELECT t.id,t.user_id FROM password_setup_tokens t JOIN user_account u ON u.id=t.user_id WHERE t.token_hash=? AND t.used_at IS NULL AND t.expires_at>CURRENT_TIMESTAMP AND u.is_active=1 FOR UPDATE",hash(token));
        if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"This password link is invalid or has expired");
        long userId=WorkAccess.number(rows.getFirst().get("user_id"));
        access.jdbc.update("UPDATE user_account SET password=?,credential_version=credential_version+1,password_setup_required=0,email_verified=1 WHERE id=?",passwords.encode(password),userId);
        access.jdbc.update("UPDATE password_setup_tokens SET used_at=CURRENT_TIMESTAMP WHERE user_id=? AND used_at IS NULL",userId);
        access.jdbc.update("DELETE FROM account_mail_queue WHERE user_id=?",userId);
    }
    public static void validatePassword(String password){if(password==null||password.length()<12||password.getBytes(StandardCharsets.UTF_8).length>72)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Use a password with at least 12 characters and at most 72 bytes");}
    private static String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
}
