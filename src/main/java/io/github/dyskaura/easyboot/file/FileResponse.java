package io.github.dyskaura.easyboot.file;

import java.time.Instant;

public record FileResponse(
        String id, String originalName, String contentType, long size,
        String uploader, Instant createdAt, String downloadUrl
) {
    static FileResponse from(StoredFile file) {
        return new FileResponse(file.getId(), file.getOriginalName(), file.getContentType(),
                file.getSize(), file.getUploader(), file.getCreatedAt(), "/api/files/" + file.getId());
    }
}
