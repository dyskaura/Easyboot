package io.github.dyskaura.easyboot.dictionary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DictionaryItemRequest(
        @NotBlank @Size(max = 100) String label,
        @NotBlank @Size(max = 100) String value,
        int sortOrder,
        boolean enabled
) {
}
