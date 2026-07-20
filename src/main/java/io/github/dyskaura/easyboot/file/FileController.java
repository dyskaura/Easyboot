package io.github.dyskaura.easyboot.file;

import io.github.dyskaura.easyboot.common.ApiResponse;
import io.github.dyskaura.easyboot.log.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation("上传文件")
    public ApiResponse<FileResponse> upload(@RequestPart("file") MultipartFile file, Authentication authentication) {
        return ApiResponse.success("上传成功", service.store(file, authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> download(@PathVariable String id) {
        FileStorageService.Download download = service.download(id);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(download.metadata().getOriginalName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.metadata().getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(download.resource());
    }
}
