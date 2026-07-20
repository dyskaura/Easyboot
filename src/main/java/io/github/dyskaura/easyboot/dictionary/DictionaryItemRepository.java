package io.github.dyskaura.easyboot.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DictionaryItemRepository extends JpaRepository<DictionaryItem, Long> {
    List<DictionaryItem> findByTypeCodeAndEnabledTrueOrderBySortOrder(String typeCode);
    List<DictionaryItem> findByTypeCodeOrderBySortOrder(String typeCode);
    void deleteByTypeCode(String typeCode);
}
