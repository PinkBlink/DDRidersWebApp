package org.riders.sharing.service.impl;

import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.service.ScooterService;

import java.util.UUID;
import java.util.logging.Logger;

public class ScooterServiceImpl implements ScooterService {
    private final ScooterRepository scooterRepository;
    private final Logger logger;

    public ScooterServiceImpl(ScooterRepository scooterRepository, Logger logger) {
        this.scooterRepository = scooterRepository;
        this.logger = logger;
    }

    public Scooter update(Scooter scooter) {
        if (scooterRepository.isExists(scooter)) {
            return scooterRepository.update(scooter);
        }
        throw new ElementNotFoundException("Attempt to update non-existent scooter id = %s"
                .formatted(scooter.getId()));
    }

    @Override
    public Scooter getById(UUID id) {
        return scooterRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Couldn't find scooter with id %s"
                        .formatted(id)));
    }
}
