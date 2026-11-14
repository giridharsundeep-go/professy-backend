package com.greatleyposhley.professy.collaboration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.net.URI;
import java.net.http.*;
import java.security.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Avatars are sanitized, public-by-link images. Ticket/document files never use this endpoint. */
@RestController
public class AvatarMedia {
 private final WorkAccess access;
 private final Path directory;
 private final String driver, publicUrl, bucket, region, endpoint, accessKey, secretKey, sessionToken;
 public AvatarMedia(WorkAccess access,
  @Value("${app.media.driver:local}") String driver,
  @Value("${app.media.directory:${user.home}/.local/share/professy/avatars}") String directory,
  @Value("${app.media.public-base-url:http://localhost:8080/media/avatars}") String publicUrl,
  @Value("${app.media.s3.bucket:}") String bucket,
  @Value("${app.media.s3.region:us-east-1}") String region,
  @Value("${app.media.s3.endpoint:}") String endpoint,
  @Value("${app.media.s3.access-key:${AWS_ACCESS_KEY_ID:}}") String accessKey,
  @Value("${app.media.s3.secret-key:${AWS_SECRET_ACCESS_KEY:}}") String secretKey,
  @Value("${app.media.s3.session-token:${AWS_SESSION_TOKEN:}}") String sessionToken) {
  this.access=access;this.driver=driver;this.directory=Path.of(directory).toAbsolutePath().normalize();
  this.publicUrl=publicUrl.replaceAll("/+$","");this.bucket=bucket;this.region=region;this.endpoint=endpoint;this.accessKey=accessKey;this.secretKey=secretKey;this.sessionToken=sessionToken;
  if(!Set.of("local","s3").contains(driver))throw new IllegalArgumentException("Unknown media storage driver");
  if(!publicUrl.startsWith("https://") && !publicUrl.matches("http://(localhost|127\\.0\\.0\\.1)(:[0-9]+)?/.*"))throw new IllegalArgumentException("Use HTTPS for the media public URL");
 }
 @PostMapping(value={"/api/user/upload-avatar","/api/users/upload-avatar"},consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
 public Map<String,Object> upload(@RequestParam("file") MultipartFile file) {
  access.actor(); // The user update endpoint separately enforces who may change the profile.
  try {
   if(file.isEmpty()||file.getSize()>5*1024*1024)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a photo under 5 MB");
   byte[] png;try{png=sanitize(file.getBytes());}catch(IOException invalid){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose an intact PNG or JPEG image");}String key=UUID.randomUUID()+".png";
   if(driver.equals("s3"))putS3(key,png);else {Files.createDirectories(directory);Files.write(directory.resolve(key),png,StandardOpenOption.CREATE_NEW);}
   return Map.of("data",Map.of("profilePictureUrl",publicUrl+"/"+key));
  }catch(ResponseStatusException e){throw e;}catch(Exception e){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Photo storage is unavailable. Your profile has not been submitted.");}
 }
 static byte[] sanitize(byte[] source) throws IOException {
  if(source.length==0||source.length>5*1024*1024)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a PNG or JPEG image under 5 MB");
  try(var input=ImageIO.createImageInputStream(new ByteArrayInputStream(source))) {
   var readers=ImageIO.getImageReaders(input);if(!readers.hasNext())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a valid PNG or JPEG image");
   var reader=readers.next();try{reader.setInput(input);String format=reader.getFormatName().toLowerCase(Locale.ROOT);
    if(!Set.of("png","jpeg","jpg").contains(format))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only PNG and JPEG photos are supported");
    int w=reader.getWidth(0),h=reader.getHeight(0);if(w<1||h<1||(long)w*h>20_000_000L)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Photo dimensions are too large");
    BufferedImage image=reader.read(0);int size=Math.min(1024,Math.max(w,h));double scale=(double)size/Math.max(w,h);
    var clean=new BufferedImage(Math.max(1,(int)(w*scale)),Math.max(1,(int)(h*scale)),BufferedImage.TYPE_INT_ARGB);var graphics=clean.createGraphics();try{graphics.drawImage(image,0,0,clean.getWidth(),clean.getHeight(),null);}finally{graphics.dispose();}
    var output=new ByteArrayOutputStream();ImageIO.write(clean,"png",output);return output.toByteArray();
   }finally{reader.dispose();}
  }
 }
 @GetMapping("/media/avatars/{key}") public ResponseEntity<byte[]> photo(@PathVariable String key) throws IOException {
  if(!driver.equals("local")||!key.matches("[0-9a-f-]{36}\\.png"))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  Path file=directory.resolve(key).normalize();if(!file.getParent().equals(directory)||!Files.isRegularFile(file,LinkOption.NOFOLLOW_LINKS))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).header("X-Content-Type-Options","nosniff").header("Content-Security-Policy","default-src 'none'; sandbox").cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic()).body(Files.readAllBytes(file));
 }
 private void putS3(String key,byte[] bytes)throws Exception {
  if(!bucket.matches("[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]")||!region.matches("[a-z0-9-]+")||accessKey.isBlank()||secretKey.isBlank())throw new IllegalStateException("Configure S3 credentials and bucket");
  URI base=URI.create(endpoint.isBlank()?"https://"+bucket+".s3."+region+".amazonaws.com":endpoint);
  if(!"https".equals(base.getScheme()) && !("http".equals(base.getScheme()) && Set.of("localhost","127.0.0.1").contains(base.getHost())))throw new IllegalStateException("S3 requires HTTPS");
  URI target=URI.create(base.toString().replaceAll("/+$","")+(endpoint.isBlank()?"/":"/"+bucket+"/")+"avatars/"+key);
  String date=DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC).format(Instant.now()),day=date.substring(0,8),payload=hash(bytes);
  var headers=new TreeMap<String,String>();headers.put("host",target.getRawAuthority());headers.put("x-amz-content-sha256",payload);headers.put("x-amz-date",date);if(!sessionToken.isBlank())headers.put("x-amz-security-token",sessionToken);
  String signed=String.join(";",headers.keySet()),canonicalHeaders=headers.entrySet().stream().map(e->e.getKey()+":"+e.getValue()+"\n").reduce("",String::concat);
  String canonical="PUT\n"+target.getRawPath()+"\n\n"+canonicalHeaders+"\n"+signed+"\n"+payload,scope=day+"/"+region+"/s3/aws4_request";
  byte[] k=mac(("AWS4"+secretKey).getBytes(java.nio.charset.StandardCharsets.UTF_8),day);k=mac(k,region);k=mac(k,"s3");k=mac(k,"aws4_request");String signature=HexFormat.of().formatHex(mac(k,"AWS4-HMAC-SHA256\n"+date+"\n"+scope+"\n"+hash(canonical.getBytes(java.nio.charset.StandardCharsets.UTF_8))));
  var request=java.net.http.HttpRequest.newBuilder(target).timeout(Duration.ofSeconds(20)).header("Content-Type","image/png").header("Authorization","AWS4-HMAC-SHA256 Credential="+accessKey+"/"+scope+", SignedHeaders="+signed+", Signature="+signature).PUT(java.net.http.HttpRequest.BodyPublishers.ofByteArray(bytes));
  headers.forEach((name,value)->{if(!name.equals("host"))request.header(name,value);});
  var result=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).followRedirects(HttpClient.Redirect.NEVER).build().send(request.build(),HttpResponse.BodyHandlers.discarding());
  if(result.statusCode()<200||result.statusCode()>=300)throw new IOException("Object storage refused upload");
 }
 private static String hash(byte[] bytes)throws Exception{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));}
 private static byte[] mac(byte[] key,String text)throws Exception{var h=Mac.getInstance("HmacSHA256");h.init(new SecretKeySpec(key,"HmacSHA256"));return h.doFinal(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));}
}
