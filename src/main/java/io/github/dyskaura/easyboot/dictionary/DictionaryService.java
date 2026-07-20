package io.github.dyskaura.easyboot.dictionary;

import io.github.dyskaura.easyboot.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictionaryService {
    private final DictionaryTypeRepository typeRepository;
    private final DictionaryItemRepository itemRepository;

    @Transactional(readOnly = true)
    public List<DictionaryItem> publicItems(String code) {
        DictionaryType type = typeRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "字典类型不存在"));
        if (!type.isEnabled()) throw new BusinessException(HttpStatus.NOT_FOUND, "字典类型已停用");
        return itemRepository.findByTypeCodeAndEnabledTrueOrderBySortOrder(code);
    }

    @Transactional
    public DictionaryType saveType(Long id, DictionaryTypeRequest request) {
        DictionaryType type = id == null ? new DictionaryType() : typeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "字典类型不存在"));
        if (id == null && typeRepository.existsByCode(request.code())) {
            throw new BusinessException(HttpStatus.CONFLICT, "字典编码已存在");
        }
        if (id != null && !type.getCode().equals(request.code())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "字典编码创建后不能修改");
        }
        type.setCode(request.code());
        type.setName(request.name().trim());
        type.setDescription(request.description());
        type.setEnabled(request.enabled());
        return typeRepository.save(type);
    }

    @Transactional
    public DictionaryItem saveItem(String typeCode, Long id, DictionaryItemRequest request) {
        if (!typeRepository.existsByCode(typeCode)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "字典类型不存在");
        }
        DictionaryItem item = id == null ? new DictionaryItem() : itemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "字典项不存在"));
        item.setTypeCode(typeCode);
        item.setLabel(request.label().trim());
        item.setItemValue(request.value().trim());
        item.setSortOrder(request.sortOrder());
        item.setEnabled(request.enabled());
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteType(Long id) {
        DictionaryType type = typeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "字典类型不存在"));
        itemRepository.deleteByTypeCode(type.getCode());
        typeRepository.delete(type);
    }
}
