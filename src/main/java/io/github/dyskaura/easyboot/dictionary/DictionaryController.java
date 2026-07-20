package io.github.dyskaura.easyboot.dictionary;

import io.github.dyskaura.easyboot.common.ApiResponse;
import io.github.dyskaura.easyboot.log.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {
    private final DictionaryService service;
    private final DictionaryTypeRepository typeRepository;
    private final DictionaryItemRepository itemRepository;

    @GetMapping("/{code}/items")
    public ApiResponse<List<DictionaryItem>> publicItems(@PathVariable String code) {
        return ApiResponse.success(service.publicItems(code));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DictionaryType>> types() {
        return ApiResponse.success(typeRepository.findAll());
    }

    @GetMapping("/{code}/all-items")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<DictionaryItem>> allItems(@PathVariable String code) {
        return ApiResponse.success(itemRepository.findByTypeCodeOrderBySortOrder(code));
    }

    @PostMapping
    @Operation("创建字典类型")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DictionaryType> createType(@Valid @RequestBody DictionaryTypeRequest request) {
        return ApiResponse.success("创建成功", service.saveType(null, request));
    }

    @PutMapping("/{id}")
    @Operation("更新字典类型")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DictionaryType> updateType(@PathVariable Long id,
                                                   @Valid @RequestBody DictionaryTypeRequest request) {
        return ApiResponse.success("更新成功", service.saveType(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation("删除字典类型")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteType(@PathVariable Long id) {
        service.deleteType(id);
        return ApiResponse.success("删除成功", null);
    }

    @PostMapping("/{code}/items")
    @Operation("创建字典项")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DictionaryItem> createItem(@PathVariable String code,
                                                   @Valid @RequestBody DictionaryItemRequest request) {
        return ApiResponse.success("创建成功", service.saveItem(code, null, request));
    }

    @PutMapping("/{code}/items/{id}")
    @Operation("更新字典项")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DictionaryItem> updateItem(@PathVariable String code, @PathVariable Long id,
                                                   @Valid @RequestBody DictionaryItemRequest request) {
        return ApiResponse.success("更新成功", service.saveItem(code, id, request));
    }

    @DeleteMapping("/items/{id}")
    @Operation("删除字典项")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        itemRepository.deleteById(id);
        return ApiResponse.success("删除成功", null);
    }
}
