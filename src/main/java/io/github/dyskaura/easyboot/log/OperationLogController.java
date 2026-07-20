package io.github.dyskaura.easyboot.log;

import io.github.dyskaura.easyboot.common.ApiResponse;
import io.github.dyskaura.easyboot.common.PageResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OperationLogController {
    private final OperationLogRepository repository;

    @GetMapping
    public ApiResponse<PageResponse<OperationLog>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return ApiResponse.success(PageResponse.from(repository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        )));
    }
}
