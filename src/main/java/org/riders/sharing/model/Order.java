package org.riders.sharing.model;

import org.riders.sharing.model.enums.OrderStatus;

import java.time.Period;
import java.util.Objects;

public class Order {
    private int id;
    private Customer customer;
    private Scooter scooter;
    private Period rentPeriod;
    private OrderStatus status;

    public Order(int id, Customer customer, Scooter scooter, Period rentPeriod) {
        this.id = id;
        this.customer = customer;
        this.scooter = scooter;
        this.rentPeriod = rentPeriod;
        this.status = OrderStatus.ONGOING;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Scooter getScooter() {
        return scooter;
    }

    public void setScooter(Scooter scooter) {
        this.scooter = scooter;
    }

    public Period getRentPeriod() {
        return rentPeriod;
    }

    public void setRentPeriod(Period rentPeriod) {
        this.rentPeriod = rentPeriod;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order that)) {
            return false;
        }
        return id == that.id
                && status == that.status
                && Objects.equals(customer, that.customer)
                && Objects.equals(scooter, that.scooter)
                && Objects.equals(rentPeriod, that.rentPeriod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, scooter, rentPeriod, status);
    }
}
