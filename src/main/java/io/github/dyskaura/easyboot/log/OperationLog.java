package io.github.dyskaura.easyboot.log;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_operation_log")
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String operation;
    @Column(nullable = false, length = 100)
    private String username;
    @Column(nullable = false, length = 10)
    private String method;
    @Column(nullable = false, length = 255)
    private String path;
    @Column(length = 64)
    private String ip;
    @Column(nullable = false)
    private boolean success;
    @Column(length = 500)
    private String errorMessage;
    @Column(nullable = false)
    private long durationMs;
    @Column(nullable = false)
    private Instant createdAt;
}
