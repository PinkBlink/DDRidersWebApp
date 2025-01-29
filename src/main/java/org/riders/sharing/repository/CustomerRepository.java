package org.riders.sharing.repository;

import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseRepository {
    //create
    void saveCustomer(Customer customer) throws RepositoryException;

    //update
    void changeCustomerName(int customerId, String name);

    void changeCustomerSurname(int customerId, String surname);

    void changeCustomerEmail(int customerId, String email);

    void changeCustomerPassword(int customerId, String password);

    //read
    Optional<Customer> findCustomerById(int id) throws RepositoryException;

    Optional<Customer> findCustomerByEmail(String email) throws RepositoryException;

    List<Customer> findAll() throws RepositoryException;

    //delete
    void deleteCustomer(Customer customer) throws RepositoryException;
}
