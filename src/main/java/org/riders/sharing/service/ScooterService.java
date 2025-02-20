package org.riders.sharing.service;

import org.riders.sharing.model.Scooter;

public interface ScooterService extends BaseService<Scooter> {
    Scooter update(Scooter scooter);
}
