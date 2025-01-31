package org.riders.sharing.repository;

import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository {
    //create
    void saveCustomer(Customer customer) throws RepositoryException;

    //update
    void updateCustomer(Customer customer) throws RepositoryException;

    //read
    Optional<Customer> findCustomerById(int id) throws RepositoryException;

    List<Customer> findAll() throws RepositoryException;

    //delete
    void deleteCustomer(int customerId) throws RepositoryException;
}
