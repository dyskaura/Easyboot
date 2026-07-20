package io.github.dyskaura.easyboot.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DictionaryTypeRepository extends JpaRepository<DictionaryType, Long> {
    Optional<DictionaryType> findByCode(String code);
    boolean existsByCode(String code);
}
