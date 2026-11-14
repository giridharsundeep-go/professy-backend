package com.greatleyposhley.professy.collaboration;

import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.services.OrgService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AccountAccessController {
    private final WorkAccess access;private final PasswordSetupService setup;private final UserAccountRepository users;private final OrgService orgs;private final PasswordEncoder passwords;
    public AccountAccessController(WorkAccess access,PasswordSetupService setup,UserAccountRepository users,OrgService orgs,PasswordEncoder passwords){this.access=access;this.setup=setup;this.users=users;this.orgs=orgs;this.passwords=passwords;}
    @GetMapping("/session/access") public Object session(){var u=access.actor();return Map.of("user",u,"isAdmin",access.admin(u),"projectIds",access.projects(u),"permissions",access.policy.effective(u),"roles",access.policy.assigned(u.getOrgId(),u.getId()));}
    @PostMapping("/auth/password/request") public Object request(@RequestBody Map<String,String> body,HttpServletRequest request){setup.request(body.get("email"),request.getRemoteAddr());return Map.of("message","If an active account exists, a password email has been queued.");}
    @PostMapping("/auth/password/reset") public Object reset(@RequestBody Map<String,String> body){setup.reset(body.get("token"),body.get("password"));return Map.of("message","Password saved. You can now sign in.");}
    @PostMapping("/auth/email/verify") public Object verifyEmail(@RequestBody Map<String,String> body){String passwordSetupUrl=setup.verifyEmail(body.get("token"));return passwordSetupUrl==null?Map.of("success",true,"emailVerified",true):Map.of("success",true,"emailVerified",true,"passwordSetupUrl",passwordSetupUrl);}
    @PostMapping("/users/{id}/verification-email") public Object sendVerification(@PathVariable long id){var actor=access.actor();var user=users.findById(id).filter(candidate->Objects.equals(candidate.getOrgId(),actor.getOrgId())).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));if(!Objects.equals(actor.getId(),user.getId()))access.require("USERS.UPDATE");if(user.getEmailVerified())return Map.of("queued",false,"message","This email address is already verified.");setup.cancelVerificationMail(user.getId());return Map.of("queued",true,"message","Verification link created.","verificationUrl",setup.verificationLink(user.getId()));}
    @PostMapping("/users/{id}/invitation-link") public Object invitation(@PathVariable long id){access.requireAdmin();var u=users.findById(id).filter(user->user.getOrgId().equals(access.actor().getOrgId())).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));return Map.of("url",setup.link(u.getId()),"expiresInHours",24);}
    @PostMapping("/users/send-registration-email") public Object invite(@RequestBody Map<String,Object> body){access.requireAdmin();long id=WorkAccess.number(body.get("userId"));if(id==0&&body.get("email")!=null)id=users.findByEmail(body.get("email").toString()).map(UserAccount::getId).orElse(0L);final long userId=id;users.findById(userId).filter(u->u.getOrgId().equals(access.actor().getOrgId())).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));setup.queue(userId,"SETUP");return Map.of("queued",true,"message","Invitation queued for delivery when SMTP is configured.");}
    public record Registration(Org organisation,UserAccount user){}
    @PostMapping("/auth/register") @Transactional public Object register(@RequestBody Registration request){
        var user=request.user();if(user==null||request.organisation()==null)throw new IllegalArgumentException("Organization and owner are required");
        PasswordSetupService.validatePassword(user.getPassword());
        if(user.getFirstName()==null||user.getFirstName().isBlank()||user.getLastName()==null||user.getLastName().isBlank()||user.getEmail()==null||!user.getEmail().contains("@")||user.getPhone()==null||user.getGender()==null)throw new IllegalArgumentException("Complete the owner details");
        String email=user.getEmail().trim().toLowerCase(Locale.ROOT);if(users.findByEmail(email).isPresent())throw new IllegalArgumentException("Email is already registered");
        var org=orgs.createOrganisation(request.organisation());user.setId(null);user.setOrgId(org.getId());user.setOrgRole(OrgRole.SUPER_USER);user.setRoleId(null);user.setManagerId(null);user.setEmail(email);user.setIsActive(true);user.setCredentialVersion(0);user.setPasswordSetupRequired(false);user.setPassword(passwords.encode(user.getPassword()));users.save(user);setup.queue(user.getId(),"EMAIL_VERIFICATION");access.policy.seed(org.getId());
        return Map.of("success",true,"data",Map.of("org",org,"user",user));
    }
}
