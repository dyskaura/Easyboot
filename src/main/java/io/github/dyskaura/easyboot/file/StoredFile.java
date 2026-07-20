package io.github.dyskaura.easyboot.file;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_file")
public class StoredFile {
    @Id
    private String id;
    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false)
    private String storedName;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private long size;
    @Column(nullable = false)
    private String uploader;
    @Column(nullable = false)
    private Instant createdAt;
}
