package io.github.dyskaura.easyboot.dictionary;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_dictionary_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"typeCode", "itemValue"}))
public class DictionaryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String typeCode;
    @Column(nullable = false, length = 100)
    private String label;
    @Column(nullable = false, length = 100)
    private String itemValue;
    @Column(nullable = false)
    private int sortOrder;
    @Column(nullable = false)
    private boolean enabled;
}
