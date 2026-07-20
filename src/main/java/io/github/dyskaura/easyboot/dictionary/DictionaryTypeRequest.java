package io.github.dyskaura.easyboot.dictionary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DictionaryTypeRequest(
        @NotBlank @Pattern(regexp = "^[a-z][a-z0-9_]*$") @Size(max = 50) String code,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description,
        boolean enabled
) {
}
