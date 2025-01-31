package org.riders.sharing.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;


public class Controller {
    public static void main(String[] args) throws RepositoryException {
        Logger logger = LogManager.getLogger(Controller.class);
        logger.info("Dirty Dick Riders forever");
        CustomerRepository customerRepository = new CustomerRepositoryImpl();
        ScooterRepository scooterRepository = new ScooterRepositoryImpl();
//        Customer customer1 = new Customer(1,"John","Malkovich", "malkovich.john@fakemail.lol","qweq121erqwrkb12142@24");
//        Customer customer2 = new Customer(2, "Alice", "Wonderland", "alice.wonder@fakemail.lol", "pa$$w0rd123!");
//        Customer customer3 = new Customer(3, "Bob", "Marley", "bob.marley@fakemail.lol", "reggaeForever42");
//        Customer customer4 = new Customer(4, "Charlie", "Chaplin", "charlie.chaplin@fakemail.lol", "silentComedy@1920");
//        Customer customer5 = new Customer(5, "Diana", "Prince", "diana.prince@fakemail.lol", "wonderWoman!77");
//        Customer customer6 = new Customer(6, "Elon", "Musk", "elon.musk@fakemail.lol", "spacexMars2040");
//        repository.saveCustomer(customer1);
//        repository.saveCustomer(customer2);
//        repository.saveCustomer(customer3);
//        repository.saveCustomer(customer4);
//        repository.saveCustomer(customer5);
//        repository.saveCustomer(customer6);
    }
}