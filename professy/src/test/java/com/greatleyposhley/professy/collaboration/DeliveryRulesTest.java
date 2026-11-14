package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class DeliveryRulesTest {
 @Test void crossProjectArchitectureNodeIsRejected(){assertThrows(IllegalArgumentException.class,()->DeliveryRules.graph(Map.of("nodes",List.of(Map.of("id",2,"x",0,"y",0)),"edges",List.of()),Set.of(1L)));}
 @Test void duplicateNodesAreRejected(){var n=Map.of("id",1,"x",0,"y",0);assertThrows(IllegalArgumentException.class,()->DeliveryRules.graph(Map.of("nodes",List.of(n,n),"edges",List.of()),Set.of(1L)));}
 @Test void nonFiniteCoordinatesAreRejected(){assertThrows(IllegalArgumentException.class,()->DeliveryRules.graph(Map.of("nodes",List.of(Map.of("id",1,"x",Double.NaN,"y",0)),"edges",List.of()),Set.of(1L)));}
 @Test void edgesCannotEscapeTheGraph(){assertThrows(IllegalArgumentException.class,()->DeliveryRules.graph(Map.of("nodes",List.of(Map.of("id",1,"x",0,"y",0)),"edges",List.of(Map.of("from",1,"to",2,"type","HTTP"))),Set.of(1L,2L)));}
 @Test void validatesAServiceDependency(){var g=DeliveryRules.graph(Map.of("nodes",List.of(Map.of("id",1,"x",0,"y",0),Map.of("id",2,"x",100,"y",100)),"edges",List.of(Map.of("from",1,"to",2,"type","EVENT"))),Set.of(1L,2L));assertEquals(1,((List<?>)g.get("edges")).size());}
 @Test void traversalAndDuplicateEvidenceAreRejected(){assertThrows(IllegalArgumentException.class,()->DeliveryRules.files(List.of(Map.of("path","../secret.py","content","x"))));assertThrows(IllegalArgumentException.class,()->DeliveryRules.files(List.of(Map.of("path","implementation.py","content","x"),Map.of("path","implementation.py","content","y"))));}
 @Test void bothCodeAndTestsAreRequired(){assertThrows(IllegalArgumentException.class,()->DeliveryRules.files(List.of(Map.of("path","implementation.py","content","x"))));assertEquals(2,DeliveryRules.files(List.of(Map.of("path","implementation.py","content","x"),Map.of("path","test_implementation.py","content","x"))).size());}
 @Test void workerCredentialHashIsStableAndNotTheSecret(){String token="test-credential";assertNotEquals(token,DeliveryOperations.hash(token));assertEquals(64,DeliveryOperations.hash(token).length());assertEquals(DeliveryOperations.hash(token),DeliveryOperations.hash(token));}
}
