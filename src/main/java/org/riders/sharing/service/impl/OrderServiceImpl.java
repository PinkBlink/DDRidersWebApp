package org.riders.sharing.service.impl;

import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.exception.ServiceException;
import org.riders.sharing.model.Customer;
import org.riders.sharing.model.Order;
import org.riders.sharing.repository.OrderRepository;

import java.util.Optional;

public class OrderServiceImpl {
    private OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> getOngoingOrder(Customer customer) throws ServiceException {
        try {
            return orderRepository.findOngoingOrderByCustomer(customer);
        } catch (RepositoryException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

}
