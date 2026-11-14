package com.greatleyposhley.professy.collaboration;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.services.JwtService;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.security.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
@RestController @RequestMapping("/api")
public class AccountHandoffController {
 private final WorkAccess access;private final JdbcTemplate jdbc;private final UserAccountRepository users;private final JwtService jwt;
 public AccountHandoffController(WorkAccess access,JdbcTemplate jdbc,UserAccountRepository users,JwtService jwt){this.access=access;this.jdbc=jdbc;this.users=users;this.jwt=jwt;}
 private String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
 @PostMapping("/session/handoff")@Transactional public Object create(){var user=access.actor();byte[] bytes=new byte[32];new SecureRandom().nextBytes(bytes);String code=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);jdbc.update("DELETE FROM account_handoff_codes WHERE expires_at<CURRENT_TIMESTAMP OR (user_id=? AND used_at IS NOT NULL)",user.getId());jdbc.update("INSERT INTO account_handoff_codes(code_hash,user_id,credential_version,expires_at) VALUES(?,?,?,TIMESTAMPADD(SECOND,60,CURRENT_TIMESTAMP))",hash(code),user.getId(),user.getCredentialVersion());return Map.of("code",code,"expiresInSeconds",60);}
 @PostMapping("/auth/handoff/consume")@Transactional public Object consume(@RequestBody Map<String,String> body){String code=body.getOrDefault("code","");if(code.length()<32||code.length()>100)throw invalid();var rows=jdbc.queryForList("SELECT user_id,credential_version FROM account_handoff_codes WHERE code_hash=? AND used_at IS NULL AND expires_at>CURRENT_TIMESTAMP FOR UPDATE",hash(code));if(rows.isEmpty())throw invalid();var row=rows.getFirst();var user=users.findById(WorkAccess.number(row.get("user_id"))).filter(u->Boolean.TRUE.equals(u.getIsActive())&&!u.getPasswordSetupRequired()&&u.getCredentialVersion()==WorkAccess.number(row.get("credential_version"))).orElseThrow(this::invalid);jdbc.update("UPDATE account_handoff_codes SET used_at=CURRENT_TIMESTAMP WHERE code_hash=?",hash(code));return Map.of("success",true,"data",Map.of("token",jwt.generateToken(user),"user",user));}
 private ResponseStatusException invalid(){return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"This sign-in handoff has expired. Open Chatly again or sign in.");}
}
