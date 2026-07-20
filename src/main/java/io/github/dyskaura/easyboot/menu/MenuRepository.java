package io.github.dyskaura.easyboot.menu;

import io.github.dyskaura.easyboot.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByEnabledTrueAndRequiredRoleInOrderBySortOrder(List<Role> roles);
    List<Menu> findAllByOrderBySortOrder();
}
