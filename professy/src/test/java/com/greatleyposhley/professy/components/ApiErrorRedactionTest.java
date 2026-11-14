package com.greatleyposhley.professy.components;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class ApiErrorRedactionTest {
 private final ApiErrorRedaction advice=new ApiErrorRedaction();
 @Test void databaseDetailsDoNotEscapeLegacyControllers(){var res=new MockHttpServletResponse();res.setStatus(500);var body=Map.of("message","JDBC SQL [INSERT INTO users VALUES ('sensitive')]","details","private","data","sensitive","success",false);Object result=advice.beforeBodyWrite(body,null,null,null,null,new ServletServerHttpResponse(res));String json=result.toString();assertFalse(json.contains("JDBC"));assertFalse(json.contains("sensitive"));assertFalse(json.contains("private"));assertTrue(json.contains("success=false"));}
 @Test void helpfulValidationMessagesRemainAvailable(){var res=new MockHttpServletResponse();res.setStatus(400);var body=Map.of("message","Choose a sprint");assertSame(body,advice.beforeBodyWrite(body,null,null,null,null,new ServletServerHttpResponse(res)));}
 @Test void successfulResponsesAreUnchanged(){var res=new MockHttpServletResponse();var body=Map.of("data",Map.of("title","SQL planning"));assertSame(body,advice.beforeBodyWrite(body,null,null,null,null,new ServletServerHttpResponse(res)));}
}
