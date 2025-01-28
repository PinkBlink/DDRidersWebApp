package org.riders.sharing.factory;

import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.ScooterRent;

import java.time.Period;

public interface ScooterRentFactory {
    ScooterRent createScooter(int id, Scooter scooter, Customer customer, Period period);
}
