package org.riders.sharing.factory;

import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.Order;

import java.time.Period;

public interface OrderFactory {
    Order createScooter(int id, Scooter scooter, Customer customer, Period period);
}
