package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class DeliveryWorkerTest {
 @Test void architectureLabelsAreEscapedRatherThanExecutableMarkup(){var graph=Map.<String,Object>of("nodes",List.of(Map.of("id",1,"x",40,"y",40)),"edges",List.of());String svg=DeliveryWorker.architectureSvg(graph,List.of(Map.of("id",1,"name","<script>alert(1)</script>")));assertFalse(svg.contains("<script>"));assertTrue(svg.contains("&lt;script&gt;"));assertTrue(svg.startsWith("<svg xmlns="));}

 @Test void instruResourceIdentifiersKeepTheirLabelsInEvidence(){String id="b8ecee85-05ab-4ccf-8245-cf8d55850d8b";var graph=Map.<String,Object>of("nodes",List.of(Map.of("id",id,"x",40,"y",40)),"edges",List.of());String svg=DeliveryWorker.architectureSvg(graph,List.of(Map.of("id",id,"name","Payments API")));assertTrue(svg.contains("Payments API"));}

 @Test void leaseFitsThePersistentCapabilityColumn(){String lease=DeliveryWorker.leaseToken();assertTrue(lease.matches("[a-f0-9]{64}"));assertNotEquals(lease,DeliveryWorker.leaseToken());}
 @Test void noWorkerSecretCannotClaimAnything(){var worker=new DeliveryWorker(null,null,null,"/tmp/delivery-test");assertEquals(401,assertThrows(ResponseStatusException.class,()->worker.identity(10,null)).getStatusCode().value());}
 @Test void completionLeaseIsBoundToAgentAndOrganization(){var access=new WorkAccess(null,mock(JdbcTemplate.class),null);var ops=new DeliveryOperations(access,null,null,null,null,null,"/tmp/delivery-test");var worker=new DeliveryWorker(ops,access,null,"/tmp/delivery-test");String key="x".repeat(64);when(access.jdbc.queryForList(anyString(),eq("job"),eq(10L),eq(309L))).thenReturn(List.of(new HashMap<>(Map.of("lease_token",key))));assertThrows(ResponseStatusException.class,()->worker.job(10,"job","wrong",Map.of("org_id",309L)));assertNotNull(worker.job(10,"job",key,Map.of("org_id",309L)));assertThrows(ResponseStatusException.class,()->worker.job(10,"job",key,Map.of("org_id",310L)));}
}
