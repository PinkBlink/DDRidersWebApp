package org.riders.sharing.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.factory.CustomerFactory;
import org.riders.sharing.factory.OrderFactory;
import org.riders.sharing.factory.ScooterFactory;
import org.riders.sharing.factory.impl.CustomerFactoryImpl;
import org.riders.sharing.factory.impl.OrderFactoryImpl;
import org.riders.sharing.factory.impl.ScooterFactoryImpl;
import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Order;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.model.enums.ScooterType;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.repository.impl.OrderRepositoryImpl;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;

import java.time.LocalDateTime;
import java.util.Optional;

public class Controller {
    private static OrderRepository orderRepository = new OrderRepositoryImpl();
    private static ScooterRepository scooterRepository = new ScooterRepositoryImpl();
    private static CustomerRepository customerRepository = new CustomerRepositoryImpl();

    public static void main(String[] args) throws RepositoryException {
        Logger logger = LogManager.getLogger(Controller.class);
        logger.info("Dirty Dick Riders FOREVER");


//        createAndAddCustomers();
//        createAndAddScooters();
//        createAndAddOrders();
        System.out.println(orderRepository.findAll());
        System.out.println(scooterRepository.findAll());
        System.out.println(customerRepository.findAll());
    }

    private static void createAndAddOrders() throws RepositoryException {
        Optional<Scooter> scooter1 = scooterRepository.findScooterById(1);
        Optional<Scooter> scooter2 = scooterRepository.findScooterById(2);
        Optional<Scooter> scooter3 = scooterRepository.findScooterById(3);
        Optional<Scooter> scooter4 = scooterRepository.findScooterById(4);
        Optional<Scooter> scooter5 = scooterRepository.findScooterById(5);

        OrderFactory orderFactory = new OrderFactoryImpl();
        Order order1 = orderFactory.createOrder(1, 1, scooter1.get(), LocalDateTime.now());
        Order order2 = orderFactory.createOrder(2, 2, scooter2.get(), LocalDateTime.now());
        Order order3 = orderFactory.createOrder(3, 3, scooter3.get(), LocalDateTime.now());
        Order order4 = orderFactory.createOrder(4, 4, scooter4.get(), LocalDateTime.now());
        Order order5 = orderFactory.createOrder(5, 5, scooter5.get(), LocalDateTime.now());


        OrderRepository orderRepository = new OrderRepositoryImpl();
        orderRepository.saveOrder(order1);
        orderRepository.saveOrder(order5);
        orderRepository.saveOrder(order4);
        orderRepository.saveOrder(order3);
        orderRepository.saveOrder(order2);
    }

    private static void createAndAddCustomers() throws RepositoryException {
        CustomerFactory customerFactory = new CustomerFactoryImpl();
        Customer customer1 = customerFactory
                .createCustomer(1, "John", "Malkovich", "malkovich.john@fakemail.lol", "qweq121erqwrkb12142@24");
        Customer customer2 = customerFactory
                .createCustomer(2, "Alice", "Wonderland", "alice.wonder@fakemail.lol", "pa$$w0rd123!");
        Customer customer3 = customerFactory
                .createCustomer(3, "Bob", "Marley", "bob.marley@fakemail.lol", "reggaeForever42");
        Customer customer4 = customerFactory
                .createCustomer(4, "Charlie", "Chaplin", "charlie.chaplin@fakemail.lol", "silentComedy@1920");
        Customer customer5 = customerFactory
                .createCustomer(5, "Diana", "Prince", "diana.prince@fakemail.lol", "wonderWoman!77");


        CustomerRepository customerRepository = new CustomerRepositoryImpl();
        customerRepository.saveCustomer(customer1);
        customerRepository.saveCustomer(customer2);
        customerRepository.saveCustomer(customer3);
        customerRepository.saveCustomer(customer4);
        customerRepository.saveCustomer(customer5);
    }

    private static void createAndAddScooters() throws RepositoryException {
        ScooterFactory scooterFactory = new ScooterFactoryImpl();
        Scooter scooter1 = scooterFactory.createScooter(1, ScooterType.FOLDING);
        Scooter scooter2 = scooterFactory.createScooter(2, ScooterType.LONG_RANGE);
        Scooter scooter3 = scooterFactory.createScooter(3, ScooterType.FOLDING);
        Scooter scooter4 = scooterFactory.createScooter(4, ScooterType.URBAN);
        Scooter scooter5 = scooterFactory.createScooter(5, ScooterType.LONG_RANGE);

        ScooterRepository scooterRepository = new ScooterRepositoryImpl();
        scooterRepository.saveScooter(scooter1);
        scooterRepository.saveScooter(scooter2);
        scooterRepository.saveScooter(scooter3);
        scooterRepository.saveScooter(scooter4);
        scooterRepository.saveScooter(scooter5);
    }
}