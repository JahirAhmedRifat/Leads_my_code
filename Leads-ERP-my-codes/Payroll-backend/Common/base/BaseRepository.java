package com.leads.microcube.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<Entity extends BaseEntity> extends JpaRepository<Entity, Long> {
    Optional<Entity> findByUuidAndIsDeletedFalse(String uuid);
    Optional<Entity> findByIdAndIsDeletedFalse(Long id);
    List<Entity> findAllByIsDeletedFalse();
    Page<Entity> findAllByIsDeletedFalse(Pageable pageable);
}
