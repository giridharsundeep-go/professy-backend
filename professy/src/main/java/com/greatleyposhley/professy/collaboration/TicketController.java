package com.greatleyposhley.professy.collaboration;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/tickets/{kind}/{id}")
public class TicketController {
    private final TicketCollaborationService service;
    public TicketController(TicketCollaborationService service){this.service=service;}
    @GetMapping public Object context(@PathVariable String kind,@PathVariable long id){return service.context(kind,id);}
    @GetMapping("/activity") public Object activity(@PathVariable String kind,@PathVariable long id,@RequestParam(defaultValue="0") long before){return service.activity(kind,id,before);}
    @PostMapping("/comments") public Object comment(@PathVariable String kind,@PathVariable long id,@RequestBody Map<String,String> body){return service.addComment(kind,id,body.get("content"));}
    @PutMapping("/comments/{commentId}") public void editComment(@PathVariable String kind,@PathVariable long id,@PathVariable long commentId,@RequestBody Map<String,String> body){service.changeComment(kind,id,commentId,body.get("content"),false);}
    @DeleteMapping("/comments/{commentId}") public void deleteComment(@PathVariable String kind,@PathVariable long id,@PathVariable long commentId){service.changeComment(kind,id,commentId,"",true);}
    @PostMapping("/attachments") public void upload(@PathVariable String kind,@PathVariable long id,@RequestParam("file") MultipartFile file) throws Exception{service.upload(kind,id,file);}
    @GetMapping("/attachments/{attachmentId}") public ResponseEntity<FileSystemResource> download(@PathVariable String kind,@PathVariable long id,@PathVariable long attachmentId){
        var metadata=service.file(kind,id,attachmentId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header("X-Content-Type-Options","nosniff")
            .header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.attachment().filename(metadata.get("original_name").toString(),StandardCharsets.UTF_8).build().toString())
            .body(new FileSystemResource(service.filePath(metadata)));
    }
    @GetMapping("/attachments/{attachmentId}/preview") public ResponseEntity<FileSystemResource> preview(@PathVariable String kind,@PathVariable long id,@PathVariable long attachmentId)throws Exception{
        var metadata=service.file(kind,id,attachmentId);var path=service.filePath(metadata);byte[] bytes;try(var input=java.nio.file.Files.newInputStream(path)){bytes=input.readNBytes(12);}
        String mime=bytes.length>=8&&(bytes[0]&255)==137&&bytes[1]==80&&bytes[2]==78&&bytes[3]==71?"image/png":bytes.length>=3&&(bytes[0]&255)==255&&(bytes[1]&255)==216&&(bytes[2]&255)==255?"image/jpeg":bytes.length>=6&&new String(bytes,0,6,StandardCharsets.US_ASCII).matches("GIF8[79]a")?"image/gif":bytes.length>=12&&new String(bytes,0,4,StandardCharsets.US_ASCII).equals("RIFF")&&new String(bytes,8,4,StandardCharsets.US_ASCII).equals("WEBP")?"image/webp":null;
        if(mime==null)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"This attachment cannot be previewed as an image");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mime)).header("X-Content-Type-Options","nosniff").header(HttpHeaders.CACHE_CONTROL,"private, no-store").body(new FileSystemResource(path));
    }
    @PatchMapping("/placement") @org.springframework.transaction.annotation.Transactional public Object placement(@PathVariable String kind,@PathVariable long id,@RequestBody Map<String,Object> body){service.place(kind,id,body);return Map.of("updated",true);}
    @PatchMapping("/assignment") public Object assignment(@PathVariable String kind,@PathVariable long id,@RequestBody Map<String,Object> body){service.assignUnassigned(kind,id,body);return Map.of("updated",true);}
    @DeleteMapping("/attachments/{attachmentId}") public void deleteFile(@PathVariable String kind,@PathVariable long id,@PathVariable long attachmentId){service.deleteFile(kind,id,attachmentId);}
    @PostMapping("/watch") public void watch(@PathVariable String kind,@PathVariable long id){service.watch(kind,id,true);}
    @DeleteMapping("/watch") public void unwatch(@PathVariable String kind,@PathVariable long id){service.watch(kind,id,false);}
}
