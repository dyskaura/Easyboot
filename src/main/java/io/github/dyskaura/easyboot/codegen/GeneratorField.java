package io.github.dyskaura.easyboot.codegen;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GeneratorField(
        @NotBlank @Pattern(regexp = "^[a-z][a-zA-Z0-9]*$") String name,
        @NotBlank String type,
        boolean required,
        String comment
) {
}
