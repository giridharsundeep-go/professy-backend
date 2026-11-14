package com.greatleyposhley.professy.components;
import org.junit.jupiter.api.Test;import org.springframework.mock.web.MockHttpServletRequest;import static org.junit.jupiter.api.Assertions.*;
class CorsConfigTest {
 @Test void acceptsDynamicLoopbackPortsAndRejectsExternalOrigins(){var source=new CorsConfig().corsConfigurationSource("http://localhost:[*],http://127.0.0.1:[*]");var cors=source.getCorsConfiguration(new MockHttpServletRequest("OPTIONS","/api/auth/login"));assertNotNull(cors);assertEquals("http://localhost:64836",cors.checkOrigin("http://localhost:64836"));assertEquals("http://127.0.0.1:4204",cors.checkOrigin("http://127.0.0.1:4204"));assertNull(cors.checkOrigin("https://untrusted.example"));assertEquals(Boolean.TRUE,cors.getAllowCredentials());assertTrue(cors.getAllowedHeaders().contains("Content-Type"));}
 @Test void productionCanUseAnExactOrigin(){var cors=new CorsConfig().corsConfigurationSource("https://apps.greatley.example").getCorsConfiguration(new MockHttpServletRequest("OPTIONS","/api/auth/login"));assertEquals("https://apps.greatley.example",cors.checkOrigin("https://apps.greatley.example"));assertNull(cors.checkOrigin("http://localhost:64836"));}
}
