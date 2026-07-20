package io.github.dyskaura.easyboot.file;

import io.github.dyskaura.easyboot.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private static final Set<String> ALLOWED = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx", "xls", "xlsx", "txt", "zip"
    );
    private final StoredFileRepository repository;

    @Value("${easyboot.storage.location}")
    private String location;
    @Value("${easyboot.storage.max-size}")
    private long maxSize;

    @Transactional
    public FileResponse store(MultipartFile multipartFile, String username) {
        if (multipartFile.isEmpty()) throw new BusinessException(HttpStatus.BAD_REQUEST, "请选择文件");
        if (multipartFile.getSize() > maxSize) throw new BusinessException(HttpStatus.BAD_REQUEST, "文件不能超过 10MB");
        String original = StringUtils.cleanPath(
                multipartFile.getOriginalFilename() == null ? "file" : multipartFile.getOriginalFilename()
        );
        if (original.contains("..")) throw new BusinessException(HttpStatus.BAD_REQUEST, "文件名不合法");
        String extension = extension(original);
        if (!ALLOWED.contains(extension)) throw new BusinessException(HttpStatus.BAD_REQUEST, "不支持该文件类型");
        String id = UUID.randomUUID().toString();
        String storedName = id + "." + extension;
        try {
            Path directory = Paths.get(location).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            Files.copy(multipartFile.getInputStream(), directory.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "文件保存失败");
        }
        StoredFile saved = repository.save(StoredFile.builder().id(id).originalName(original)
                .storedName(storedName).contentType(multipartFile.getContentType() == null
                        ? "application/octet-stream" : multipartFile.getContentType())
                .size(multipartFile.getSize()).uploader(username).createdAt(Instant.now()).build());
        return FileResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Download download(String id) {
        StoredFile file = repository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "文件不存在"));
        try {
            Resource resource = new UrlResource(Paths.get(location).toAbsolutePath().normalize()
                    .resolve(file.getStoredName()).toUri());
            if (!resource.exists()) throw new BusinessException(HttpStatus.NOT_FOUND, "文件已丢失");
            return new Download(file, resource);
        } catch (MalformedURLException exception) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "文件读取失败");
        }
    }

    private String extension(String name) {
        int index = name.lastIndexOf('.');
        return index < 0 ? "" : name.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    public record Download(StoredFile metadata, Resource resource) {
    }
}
