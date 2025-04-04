package org.riders.sharing.repository;

import org.riders.sharing.model.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseRepository<T extends BaseEntity> {
    T save(T entity);

    T update(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    boolean delete(UUID id);
}
