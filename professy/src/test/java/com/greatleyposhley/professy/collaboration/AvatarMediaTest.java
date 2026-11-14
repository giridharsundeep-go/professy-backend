package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.mock.web.MockMultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class AvatarMediaTest {
 @TempDir Path directory;
 private byte[] png()throws Exception{var out=new ByteArrayOutputStream();ImageIO.write(new BufferedImage(12,8,BufferedImage.TYPE_INT_RGB),"png",out);return out.toByteArray();}
 private AvatarMedia media(WorkAccess access){return new AvatarMedia(access,"local",directory.toString(),"http://localhost:8080/media/avatars","","us-east-1","","","","");}
 @Test void uploadIgnoresHostileFilenameAndPersistsSanitizedImage()throws Exception{var access=mock(WorkAccess.class);var media=media(access);var response=media.upload(new MockMultipartFile("file","../../evil.svg","text/html",png()));var data=(java.util.Map<?,?>)response.get("data");String key=data.get("profilePictureUrl").toString().substring("http://localhost:8080/media/avatars/".length());assertTrue(key.matches("[0-9a-f-]{36}\\.png"));var file=media.photo(key);assertEquals("nosniff",file.getHeaders().getFirst("X-Content-Type-Options"));assertEquals(12,ImageIO.read(new java.io.ByteArrayInputStream(file.getBody())).getWidth());verify(access).actor();}
 @Test void rejectsHtmlSvgMalformedAndOversizedPayloads(){for(byte[] bytes:new byte[][]{"<svg onload='alert(1)'/>".getBytes(),"<html>script</html>".getBytes(),new byte[]{1,2},new byte[5*1024*1024+1]})assertThrows(ResponseStatusException.class,()->AvatarMedia.sanitize(bytes));}
 @Test void traversalCannotReadLocalFiles(){assertThrows(ResponseStatusException.class,()->media(mock(WorkAccess.class)).photo("../secrets.png"));}
 @Test void missingIdentityCannotStoreAFile()throws Exception{var access=mock(WorkAccess.class);when(access.actor()).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED));assertThrows(ResponseStatusException.class,()->media(access).upload(new MockMultipartFile("file",png())));try(var files=java.nio.file.Files.list(directory)){assertEquals(0,files.count());}}
}
