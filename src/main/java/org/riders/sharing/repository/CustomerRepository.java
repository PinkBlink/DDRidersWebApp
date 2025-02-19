package org.riders.sharing.repository;

import org.riders.sharing.exception.ElementNotFoundException;
import org.riders.sharing.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends BaseRepository<Customer> {
    Optional<Customer> findByEmail(String email) throws ElementNotFoundException;

    boolean isUserExists(Customer customer) throws ElementNotFoundException;
}
