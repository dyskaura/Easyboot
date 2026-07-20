package io.github.dyskaura.easyboot.codegen;

import io.github.dyskaura.easyboot.log.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code-generator")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CodeGeneratorController {
    private final CodeGeneratorService service;

    @PostMapping(produces = "application/zip")
    @Operation("生成 CRUD 代码")
    public ResponseEntity<byte[]> generate(@Valid @RequestBody GeneratorRequest request) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(request.className() + "-crud.zip").build().toString())
                .body(service.generate(request));
    }
}
