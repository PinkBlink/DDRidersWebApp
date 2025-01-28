package org.riders.sharing.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.connection.ConnectionPull;
import org.riders.sharing.exception.RepositoryException;
import org.riders.sharing.factory.CustomerFactory;
import org.riders.sharing.factory.impl.CustomerFactoryImpl;
import org.riders.sharing.model.Customer;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.utils.constants.CustomerSqlQueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerRepositoryImpl implements CustomerRepository {
    private final Logger logger = LogManager.getLogger(this);
    private final CustomerFactory customerFactory = new CustomerFactoryImpl();
    private final ConnectionPull connectionPull = ConnectionPull.getInstance();

    @Override
    public void saveCustomer(Customer customer) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(CustomerSqlQueries.INSERT_QUERY);

            int id = customer.getId();
            String name = customer.getEmail();
            String surname = customer.getSurname();
            String email = customer.getEmail();
            String passwordHash = customer.getPasswordHash();//rework

            statement.setInt(1, id);
            setPreparedStatement(statement, customer);
            statement.execute();
        } catch (SQLException e) {
            logger.error("Can't save customer: " + customer, e);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws RepositoryException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPull.getConnection();
            statement = connection.prepareStatement(CustomerSqlQueries.UPDATE_QUERY);
            setPreparedStatement(statement, customer); //!!! continue from this
        } catch (SQLException e) {
            logger.error("Can't update customer: " + customer, e);
            throw new RepositoryException(e.getMessage(), e);
        } finally {
            connectionPull.releaseConnection(connection);
            closeStatement(statement);
        }

    }

    @Override
    public Optional<Customer> findCustomerById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        return null;
    }

    @Override
    public void deleteCustomer(Customer customer) {

    }

    private void setPreparedStatement(PreparedStatement statement, Customer customer) throws SQLException {
        statement.setString(2, customer.getName());
        statement.setString(3, customer.getSurname());
        statement.setString(4, customer.getEmail());
        statement.setString(5, customer.getPasswordHash());
    }

    public static void main(String[] args) throws RepositoryException {
        CustomerRepository repository = new CustomerRepositoryImpl();
        repository.saveCustomer(
                new CustomerFactoryImpl()
                        .createCustomer(1, "2", "3", "4", "5"));
    }
}
