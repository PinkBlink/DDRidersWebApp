package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterStatus;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.service.ScooterService;

import java.util.List;
import java.util.UUID;


public class ScooterServiceImpl implements ScooterService {
    private final ScooterRepository scooterRepository;
    private final Logger logger = LogManager.getLogger(ScooterServiceImpl.class);

    public ScooterServiceImpl(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    public Scooter update(Scooter scooter) {
        if (scooterRepository.isExists(scooter)) {
            logger.info("Scooter %s is successfully updated".formatted(scooter.getId()));
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
    @Override
    public List<Scooter> getScooterListByStatus(ScooterStatus scooterStatus){
        return scooterRepository.findScootersByStatus(scooterStatus);
    }
}
