package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.adapter.out.persistence.entity.SystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigJpaRepository extends JpaRepository<SystemConfigEntity, String> {
}
