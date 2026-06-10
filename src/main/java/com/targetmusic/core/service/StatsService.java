package com.targetmusic.core.service;

import com.targetmusic.core.domain.model.StatsResult;
import com.targetmusic.core.ports.in.StatsUseCase;
import com.targetmusic.core.ports.out.role.PermissionRepository;
import com.targetmusic.core.ports.out.role.RoleRepository;
import com.targetmusic.core.ports.out.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;

public class StatsService implements StatsUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public StatsService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StatsResult getStats() {
        return new StatsResult(
                userRepository.countAll(),
                userRepository.countEnabled(),
                userRepository.countDisabled(),
                roleRepository.countAll(),
                permissionRepository.countAll()
        );
    }
}
