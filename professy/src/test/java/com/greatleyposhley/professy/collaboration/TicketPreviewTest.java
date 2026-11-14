package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class TicketPreviewTest {
 @TempDir Path directory;
 @Test void rasterPreviewKeepsAuthenticatedResourceLookup()throws Exception{
  var service=mock(TicketCollaborationService.class);var controller=new TicketController(service);var metadata=Map.<String,Object>of("id",4L);var file=directory.resolve("image");Files.write(file,new byte[]{(byte)137,80,78,71,13,10,26,10});
  when(service.file("TASK",2,4)).thenReturn(metadata);when(service.filePath(metadata)).thenReturn(file);
  var result=controller.preview("TASK",2,4);assertEquals(MediaType.IMAGE_PNG,result.getHeaders().getContentType());assertEquals("private, no-store",result.getHeaders().getCacheControl());verify(service).file("TASK",2,4);
 }
 @Test void activeContentIsNeverServedAsAnImagePreview()throws Exception{
  var service=mock(TicketCollaborationService.class);var controller=new TicketController(service);var metadata=Map.<String,Object>of("id",4L);var file=directory.resolve("svg");Files.writeString(file,"<svg onload='alert(1)'></svg>");when(service.file("TASK",2,4)).thenReturn(metadata);when(service.filePath(metadata)).thenReturn(file);assertThrows(ResponseStatusException.class,()->controller.preview("TASK",2,4));
 }
}
