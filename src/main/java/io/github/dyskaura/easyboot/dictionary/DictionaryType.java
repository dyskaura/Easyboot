package io.github.dyskaura.easyboot.dictionary;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_dictionary_type")
public class DictionaryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(length = 255)
    private String description;
    @Column(nullable = false)
    private boolean enabled;
}
