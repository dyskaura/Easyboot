package io.github.dyskaura.easyboot.codegen;

import io.github.dyskaura.easyboot.common.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CodeGeneratorService {
    private static final Set<String> TYPES = Set.of(
            "String", "Integer", "Long", "Boolean", "Double", "BigDecimal", "LocalDate", "LocalDateTime"
    );
    private static final Map<String, String> IMPORTS = Map.of(
            "BigDecimal", "java.math.BigDecimal",
            "LocalDate", "java.time.LocalDate",
            "LocalDateTime", "java.time.LocalDateTime"
    );

    public byte[] generate(GeneratorRequest request) {
        request.fields().forEach(field -> {
            if (!TYPES.contains(field.type())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "不支持字段类型：" + field.type());
            }
        });
        String base = "src/main/java/" + request.packageName().replace('.', '/') + "/";
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(bytes, StandardCharsets.UTF_8)) {
            write(zip, base + request.className() + ".java", entity(request));
            write(zip, base + request.className() + "Repository.java", repository(request));
            write(zip, base + request.className() + "Request.java", dto(request));
            write(zip, base + request.className() + "Service.java", service(request));
            write(zip, base + request.className() + "Controller.java", controller(request));
            zip.finish();
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "代码生成失败");
        }
    }

    private String entity(GeneratorRequest r) {
        StringBuilder imports = new StringBuilder();
        r.fields().stream().map(GeneratorField::type).distinct()
                .filter(IMPORTS::containsKey).forEach(type -> imports.append("import ")
                        .append(IMPORTS.get(type)).append(";\n"));
        StringBuilder fields = new StringBuilder();
        for (GeneratorField f : r.fields()) {
            fields.append("    /** ").append(safeComment(f.comment())).append(" */\n")
                    .append("    @Column(nullable = ").append(!f.required()).append(")\n")
                    .append("    private ").append(f.type()).append(" ").append(f.name()).append(";\n\n");
        }
        return """
                package %s;

                import jakarta.persistence.*;
                import lombok.*;
                %s
                @Getter
                @Setter
                @NoArgsConstructor
                @AllArgsConstructor
                @Entity
                @Table(name = "%s")
                public class %s {
                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;

                %s}
                """.formatted(r.packageName(), imports, r.tableName(), r.className(), fields);
    }

    private String repository(GeneratorRequest r) {
        return """
                package %s;

                import org.springframework.data.jpa.repository.JpaRepository;

                public interface %sRepository extends JpaRepository<%s, Long> {
                }
                """.formatted(r.packageName(), r.className(), r.className());
    }

    private String dto(GeneratorRequest r) {
        StringBuilder imports = new StringBuilder("import jakarta.validation.constraints.*;\n");
        r.fields().stream().map(GeneratorField::type).distinct()
                .filter(IMPORTS::containsKey).forEach(type -> imports.append("import ")
                        .append(IMPORTS.get(type)).append(";\n"));
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < r.fields().size(); i++) {
            GeneratorField f = r.fields().get(i);
            if (f.required()) params.append("        @NotNull ");
            params.append(f.type()).append(" ").append(f.name());
            params.append(i == r.fields().size() - 1 ? "\n" : ",\n");
        }
        return """
                package %s;

                %s
                public record %sRequest(
                %s) {
                }
                """.formatted(r.packageName(), imports, r.className(), params);
    }

    private String service(GeneratorRequest r) {
        StringBuilder setters = new StringBuilder();
        for (GeneratorField f : r.fields()) {
            String cap = Character.toUpperCase(f.name().charAt(0)) + f.name().substring(1);
            setters.append("        entity.set").append(cap).append("(request.").append(f.name()).append("());\n");
        }
        return """
                package %s;

                import lombok.RequiredArgsConstructor;
                import org.springframework.data.domain.*;
                import org.springframework.stereotype.Service;
                import org.springframework.transaction.annotation.Transactional;

                @Service
                @RequiredArgsConstructor
                public class %sService {
                    private final %sRepository repository;

                    @Transactional(readOnly = true)
                    public Page<%s> list(int page, int size) {
                        return repository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
                    }

                    @Transactional
                    public %s save(Long id, %sRequest request) {
                        %s entity = id == null ? new %s() : repository.findById(id).orElseThrow();
                %s        return repository.save(entity);
                    }

                    @Transactional
                    public void delete(Long id) {
                        repository.deleteById(id);
                    }
                }
                """.formatted(r.packageName(), r.className(), r.className(), r.className(),
                r.className(), r.className(), r.className(), r.className(), setters);
    }

    private String controller(GeneratorRequest r) {
        String path = "/" + r.tableName().replace('_', '-');
        return """
                package %s;

                import jakarta.validation.Valid;
                import lombok.RequiredArgsConstructor;
                import org.springframework.data.domain.Page;
                import org.springframework.web.bind.annotation.*;

                @RestController
                @RequestMapping("/api%s")
                @RequiredArgsConstructor
                public class %sController {
                    private final %sService service;

                    @GetMapping
                    public Page<%s> list(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
                        return service.list(page, size);
                    }

                    @PostMapping
                    public %s create(@Valid @RequestBody %sRequest request) {
                        return service.save(null, request);
                    }

                    @PutMapping("/{id}")
                    public %s update(@PathVariable Long id, @Valid @RequestBody %sRequest request) {
                        return service.save(id, request);
                    }

                    @DeleteMapping("/{id}")
                    public void delete(@PathVariable Long id) {
                        service.delete(id);
                    }
                }
                """.formatted(r.packageName(), path, r.className(), r.className(), r.className(),
                r.className(), r.className(), r.className(), r.className());
    }

    private void write(ZipOutputStream zip, String path, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(path));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String safeComment(String value) {
        return value == null ? "" : value.replace("*/", "");
    }
}
