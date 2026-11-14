package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;import java.util.*;import static org.junit.jupiter.api.Assertions.*;
class TicketExecutionTest{
 Map<String,Object> spec(){return new HashMap<>(Map.of("repositoryId",1,"baseBranch","main","testProfile","PYTHON_UNITTEST","testDirectory","tests","allowedPaths",List.of("src","tests"),"acceptanceCriteria","The existing issue is reproduced and the regression passes"));}
 @Test void relativeSourceAndTestPathsAreAccepted(){assertEquals(List.of("src","tests"),TicketExecution.validate(spec()).get("allowedPaths"));}
 @Test void noShellOrArbitraryTestProfileCanBeRequested(){var s=spec();s.put("testProfile","bash -c env");assertThrows(IllegalArgumentException.class,()->TicketExecution.validate(s));}
 @Test void pathTraversalAndGitMetadataAreRejected(){for(String p:List.of("../secrets","/tmp/code",".git/config","src/../../config","src/.git/config")){var s=spec();s.put("allowedPaths",List.of(p));assertThrows(IllegalArgumentException.class,()->TicketExecution.validate(s));}}
 @Test void acceptanceAndRepositoryAreRequired(){var s=spec();s.put("acceptanceCriteria","");assertThrows(IllegalArgumentException.class,()->TicketExecution.validate(s));final var value=spec();value.put("repositoryId",0);assertThrows(IllegalArgumentException.class,()->TicketExecution.validate(value));}
 @Test void evidencePreservesExactWhitespace(){String body="  code\n\n";assertEquals(body,DeliveryWorker.repositoryFiles(List.of(Map.of("path","source/app.py","content",body))).get("source/app.py"));}
 @Test void rootTestDiscoveryDoesNotPermitRootEdits(){var s=spec();s.put("testDirectory",".");assertEquals(".",TicketExecution.validate(s).get("testDirectory"));s.put("allowedPaths",List.of("."));assertThrows(IllegalArgumentException.class,()->TicketExecution.validate(s));}
}
