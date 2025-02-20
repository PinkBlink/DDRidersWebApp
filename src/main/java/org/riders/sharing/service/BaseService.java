package org.riders.sharing.service;

import org.riders.sharing.model.BaseEntity;

import java.util.UUID;

public interface BaseService<T extends BaseEntity> {
    T getById(UUID id);
}
