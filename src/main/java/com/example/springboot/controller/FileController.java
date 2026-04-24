package com.example.springboot.controller;

import com.example.springboot.common.Result;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "*")
public class FileController {

    @PostMapping("/upload-image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("仅支持图片文件");
            }

            String originalName = file.getOriginalFilename();
            String ext = ".png";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + ext;
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists() && !dir.mkdirs()) {
                return Result.error("创建上传目录失败");
            }

            Path target = new File(dir, fileName).toPath();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/" + fileName;
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            data.put("name", originalName == null ? fileName : originalName);
            return Result.success("上传成功", data);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<?> getImageByFileName(@PathVariable("fileName") String fileName) {
        try {
            Path filePath = resolveImagePath(fileName);
            if (filePath == null || !Files.exists(filePath) || !Files.isReadable(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().body("图片读取失败");
        }
    }

    private Path resolveImagePath(String fileName) {
        String safeName = Paths.get(fileName).getFileName().toString();
        Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();

        List<Path> candidates = List.of(
                userDir.resolve("uploads").resolve(safeName),
                userDir.resolve("springboot").resolve("uploads").resolve(safeName),
                userDir.getParent() != null ? userDir.getParent().resolve("uploads").resolve(safeName) : userDir.resolve("uploads").resolve(safeName),
                userDir.getParent() != null ? userDir.getParent().resolve("springboot").resolve("uploads").resolve(safeName) : userDir.resolve("springboot").resolve("uploads").resolve(safeName)
        );

        for (Path candidate : candidates) {
            Path normalized = candidate.toAbsolutePath().normalize();
            if (Files.exists(normalized) && Files.isRegularFile(normalized)) {
                return normalized;
            }
        }
        return null;
    }
}
