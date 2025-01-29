package org.riders.sharing.factory.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.factory.OrderFactory;
import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.Order;

import java.time.Period;

public class OrderFactoryImpl implements OrderFactory {
    private final Logger logger = LogManager.getLogger(this);

    @Override
    public Order createScooter(int id, Scooter scooter, Customer customer, Period period) {
        Order order = new Order(id, customer, scooter, period);
        logger.info("ScooterRent has been successfully created: " + order);
        return order;
    }
}