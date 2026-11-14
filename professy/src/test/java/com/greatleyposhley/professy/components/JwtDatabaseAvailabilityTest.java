package com.greatleyposhley.professy.components;

import com.greatleyposhley.professy.entities.*;
import com.greatleyposhley.professy.repositories.UserAccountRepository;
import com.greatleyposhley.professy.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtDatabaseAvailabilityTest {
    @Test void unavailableDatabaseDoesNotTurnAValidJwtIntoA401() throws Exception {
        var users=mock(UserAccountRepository.class);
        var person=new UserAccount();person.setId(1L);person.setEmail("member@example.test");person.setOrgId(7L);person.setOrgRole(OrgRole.ORG_USER);
        String secret="test-signing-key-for-database-outages-0123456789";
        String token=new JwtService(secret,60000).generateToken(person);
        when(users.findByEmail(person.getEmail())).thenThrow(new DataAccessResourceFailureException("Pool exhausted"));
        var request=new MockHttpServletRequest("GET","/api/projects");request.addHeader("Authorization","Bearer "+token);
        var response=new MockHttpServletResponse();var chain=new MockFilterChain();
        new JwtAuthenticationFilter(secret,users).doFilter(request,response,chain);
        assertEquals(503,response.getStatus());assertEquals("3",response.getHeader("Retry-After"));assertNull(chain.getRequest());assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
