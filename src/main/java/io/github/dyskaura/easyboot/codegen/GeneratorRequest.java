package io.github.dyskaura.easyboot.codegen;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record GeneratorRequest(
        @NotBlank @Pattern(regexp = "^[a-z][a-z0-9.]*$") String packageName,
        @NotBlank @Pattern(regexp = "^[A-Z][a-zA-Z0-9]*$") String className,
        @NotBlank @Pattern(regexp = "^[a-z][a-z0-9_]*$") String tableName,
        @NotEmpty @Size(max = 30) List<@Valid GeneratorField> fields
) {
}
