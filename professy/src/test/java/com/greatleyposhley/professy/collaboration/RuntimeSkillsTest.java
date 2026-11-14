package com.greatleyposhley.professy.collaboration;
import java.util.*;import org.junit.jupiter.api.Test;import tools.jackson.databind.ObjectMapper;import static org.junit.jupiter.api.Assertions.*;
class RuntimeSkillsTest {
 final ObjectMapper json=new ObjectMapper();
 @Test void profilesAlwaysRequireTheirRealRuntimeAndRegressionTools(){assertTrue(RuntimeSkills.required(List.of(),"NODE_TEST").containsAll(List.of("node-runtime","node-test","repository-snapshot","regression-review")));assertFalse(RuntimeSkills.required(List.of(),"NODE_TEST").contains("unittest"));}
 @Test void unknownCapabilitiesCannotBeEnabled(){assertThrows(IllegalArgumentException.class,()->RuntimeSkills.selected(List.of("shell")));}
 @Test void configuredSkillsDoNotProveInstallation(){var config=Map.<String,Object>of("enabled",true,"worker_hash","enrolled","model","coder","skills_json","[\"unittest\"]");var report=Map.<String,Object>of("online",true,"model","coder","modelAvailable",true,"skills",List.of(Map.of("id","unittest","available",false)));var result=RuntimeSkills.readiness(config,report,List.of("unittest"),json);assertEquals(false,result.get("ready"));assertEquals(List.of("unittest"),result.get("missingInstalled"));}
 @Test void anotherInstalledModelCanBeUsedWithoutChangingWorkerDefault(){var config=Map.<String,Object>of("enabled",true,"worker_hash","enrolled","model","other-coder","skills_json","[]");var report=Map.<String,Object>of("online",true,"model","coder","modelAvailable",true,"availableModels",List.of("coder","other-coder"));assertEquals(true,RuntimeSkills.readiness(config,report,List.of(),json).get("ready"));}
 @Test void offlineWorkersCannotStartWork(){var config=Map.<String,Object>of("enabled",true,"worker_hash","enrolled","model","coder","skills_json","[]");assertEquals(false,RuntimeSkills.readiness(config,Map.of("online",false,"model","coder","modelAvailable",true),List.of(),json).get("ready"));}
 @Test void eventPayloadsAreBoundedAndTimestamped(){var event=Map.of("sequence",1,"event","BASELINE_STARTED","message","Checking original tests","created_at","2026-11-10T10:00:00Z");assertEquals("BASELINE_STARTED",RuntimeSkills.events(List.of(event)).getFirst().get("event"));assertThrows(IllegalArgumentException.class,()->RuntimeSkills.events(Collections.nCopies(51,event)));assertThrows(IllegalArgumentException.class,()->RuntimeSkills.events(List.of(Map.of("sequence",0,"event","START","created_at","2026-11-10T10:00:00Z"))));}
 @Test void unconfiguredAgentShowsSetupRequirementsInsteadOfFailingTicketView(){
  var config=new HashMap<String,Object>();config.put("model",null);config.put("enabled",null);config.put("skills_json",null);
  var result=RuntimeSkills.readiness(config,Map.of("online",false,"skills",List.of()),List.of("unittest"),json);
  assertEquals(false,result.get("ready"));assertTrue(((List<?>)result.get("reasons")).contains("Choose an Ollama model for this agent"));
 }
}