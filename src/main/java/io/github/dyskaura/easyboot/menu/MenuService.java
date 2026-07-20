package io.github.dyskaura.easyboot.menu;

import io.github.dyskaura.easyboot.common.BusinessException;
import io.github.dyskaura.easyboot.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository repository;

    @Transactional(readOnly = true)
    public List<Menu> current(Role role) {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        if (role == Role.ADMIN) roles.add(Role.ADMIN);
        return repository.findByEnabledTrueAndRequiredRoleInOrderBySortOrder(roles);
    }

    @Transactional(readOnly = true)
    public List<Menu> all() {
        return repository.findAllByOrderBySortOrder();
    }

    @Transactional
    public Menu save(Long id, MenuRequest request) {
        Menu menu = id == null ? new Menu() : repository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "菜单不存在"));
        menu.setParentId(request.parentId());
        menu.setName(request.name().trim());
        menu.setPath(request.path().trim());
        menu.setIcon(request.icon());
        menu.setRequiredRole(request.requiredRole());
        menu.setSortOrder(request.sortOrder());
        menu.setEnabled(request.enabled());
        return repository.save(menu);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "菜单不存在");
        }
        repository.deleteById(id);
    }
}
