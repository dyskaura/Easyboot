package io.github.dyskaura.easyboot.menu;

import io.github.dyskaura.easyboot.user.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long parentId;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, length = 100)
    private String path;
    @Column(length = 50)
    private String icon;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role requiredRole;
    @Column(nullable = false)
    private int sortOrder;
    @Column(nullable = false)
    private boolean enabled;
}
