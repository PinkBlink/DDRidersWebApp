package org.riders.sharing.factory.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.factory.ScooterFactory;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.ScooterType;

public class ScooterFactoryImpl implements ScooterFactory {
    private final Logger logger = LogManager.getLogger(this);

    @Override
    public Scooter createScooter(int id, ScooterType scooterType) {
        Scooter scooter = new Scooter(id, scooterType);
        logger.info("Scooter has been successfully created " + scooter);
        return scooter;
    }
}
