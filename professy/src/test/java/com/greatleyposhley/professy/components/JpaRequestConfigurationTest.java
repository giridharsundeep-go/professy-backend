package com.greatleyposhley.professy.components;

import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class JpaRequestConfigurationTest {
    private AnnotationConfigWebApplicationContext context;
    private MockMvc mvc;
    @Configuration @EnableWebMvc
    static class TestConfiguration {
        @Bean EntityManagerFactory factory() {
            return mock(EntityManagerFactory.class, call -> {
                if (call.getMethod().getName().equals("createEntityManager")) {
                    var entity = mock(EntityManager.class);
                    when(entity.isOpen()).thenReturn(true);
                    return entity;
                }
                return RETURNS_DEFAULTS.answer(call);
            });
        }
        @Bean TestController controller(EntityManagerFactory factory) { return new TestController(factory); }
    }
    @RestController
    static class TestController {
        private final EntityManagerFactory factory;
        TestController(EntityManagerFactory factory) { this.factory=factory; }
        @GetMapping("/ordinary-resource") String ordinary() {
            return TransactionSynchronizationManager.hasResource(factory) ? "bound" : "unbound";
        }
        @GetMapping(value="/api/workspace/settings/events",produces="text/event-stream") SseEmitter events() throws Exception {
            assertFalse(TransactionSynchronizationManager.hasResource(factory), "An event stream must not retain a request-scoped entity manager");
            var emitter=new SseEmitter(1000L);
            emitter.send(SseEmitter.event().name("ready").data("{}"));
            return emitter;
        }
    }
    @BeforeEach void setup() {
        context=new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(TestConfiguration.class,JpaRequestConfiguration.class);
        context.refresh();
        mvc=MockMvcBuilders.webAppContextSetup(context).build();
    }
    @AfterEach void close() { context.close(); }
    @Test void ordinaryRequestsKeepEntitySerializationSupport() throws Exception {
        mvc.perform(get("/ordinary-resource")).andExpect(status().isOk()).andExpect(content().string("bound"));
        assertFalse(TransactionSynchronizationManager.hasResource(context.getBean(EntityManagerFactory.class)));
    }
    @Test void eventStreamsDoNotOpenAnEntityManager() throws Exception {
        mvc.perform(get("/api/workspace/settings/events")).andExpect(request().asyncStarted()).andExpect(status().isOk());
        verify(context.getBean(EntityManagerFactory.class),never()).createEntityManager();
    }
}
