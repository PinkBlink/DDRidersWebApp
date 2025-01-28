package org.riders.sharing.factory.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.factory.ScooterRentFactory;
import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.ScooterRent;

import java.time.Period;

public class ScooterRentFactoryImpl implements ScooterRentFactory {
    private final Logger logger = LogManager.getLogger(this);

    @Override
    public ScooterRent createScooter(int id, Scooter scooter, Customer customer, Period period) {
        ScooterRent scooterRent = new ScooterRent(id, customer, scooter, period);
        logger.info("ScooterRent has been successfully created: " + scooterRent);
        return scooterRent;
    }
}
